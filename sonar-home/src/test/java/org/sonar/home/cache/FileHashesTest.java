/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.home.cache;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileHashesTest {

  SecureRandom secureRandom = new SecureRandom();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test_md5_hash() {
    assertThat(hash("sonar")).isEqualTo("D85E336D61F5344395C42126FAC239BC");

    // compare results with commons-codec
    for (int index = 0; index < 100; index++) {
      String random = randomString();
      assertThat(hash(random)).as(random).isEqualTo(
        DigestUtils.md5Hex(random).toUpperCase()
      );
    }
  }

  @Test
  public void test_toHex() {
    // upper-case
    assertThat(FileHashes.toHex("aloa_bi_bop_a_loula".getBytes())).isEqualTo("616C6F615F62695F626F705F615F6C6F756C61");

    // compare results with commons-codec
    for (int index = 0; index < 100; index++) {
      String random = randomString();
      assertThat(FileHashes.toHex(random.getBytes())).as(random).isEqualTo(
        Hex.encodeHexString(random.getBytes()).toUpperCase()
      );
    }
  }

  //@Test
  public void fail_if_file_does_not_exist() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Fail to compute hash of: /does/not/exist");

    new FileHashes().of(new File("/does/not/exist"));
  }

  @Test
  public void fail_if_stream_is_closed() throws Exception {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Fail to compute hash");

    InputStream input = mock(InputStream.class);
    when(input.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IllegalThreadStateException());
    new FileHashes().of(input);
  }

  private String randomString() {
    return new BigInteger(130, secureRandom).toString(32);
  }

  private String hash(String s) {
    InputStream in = new ByteArrayInputStream(s.getBytes());
    try {
      return new FileHashes().of(in);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }
}
