// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.kdgcommons.codec;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestHexCodec
{
    @Test
    public void testNullArray() throws Exception
    {
        HexCodec codec = new HexCodec();

        byte[] enc = codec.encode(null);
        assertTrue("encode(null) returned empty array", enc.length == 0);

        byte[] dec = codec.decode(null);
        assertTrue("decode(null) returned empty array", dec.length == 0);
    }


    @Test
    public void testEmptyArray() throws Exception
    {
        HexCodec codec = new HexCodec();

        byte[] enc = codec.encode(new byte[0]);
        assertTrue("encode(byte[0]) returned empty array", enc.length == 0);

        byte[] dec = codec.decode(new byte[0]);
        assertTrue("decode(byte[0]) returned empty array", dec.length == 0);
    }


    @Test
    public void testNullString() throws Exception
    {
        HexCodec codec = new HexCodec();

        String str = codec.toString(null);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes(null);
        assertArrayEquals("conversion to byte[]", new byte[0], dst);
    }


    @Test
    public void testEmptyString() throws Exception
    {
        HexCodec codec = new HexCodec();

        String str = codec.toString(new byte[0]);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes("");
        assertArrayEquals("conversion to byte[]", new byte[0], dst);
    }


    @Test
    public void testUnbrokenString() throws Exception
    {
        HexCodec codec = new HexCodec();

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "123456789ABCDEF0", str);

        byte[] dst = codec.toBytes(str);
        assertArrayEquals("conversion to byte[]", src, dst);
    }


    @Test
    public void testSeparator() throws Exception
    {
        HexCodec codec = new HexCodec(4, "X");

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "1234X5678X9ABCXDEF0", str);

        byte[] dst = codec.toBytes(str);
        assertArrayEquals("conversion to byte[]", src, dst);
    }


    @Test
    public void testMultibyteSeparator() throws Exception
    {
        HexCodec codec = new HexCodec(4, "XYZ");

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "1234XYZ5678XYZ9ABCXYZDEF0", str);

        byte[] dst = codec.toBytes(str);
        assertArrayEquals("conversion to byte[]", src, dst);
    }


    @Test
    public void testConversionToBytesIgnoresWhitespace() throws Exception
    {
        String str = "123456 78  9ABC\nDEF\t0";
        byte[] exp = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        byte[] dst = new HexCodec().toBytes(str);
        assertArrayEquals("conversion to byte[]", exp, dst);
    }


    @Test
    public void testConversionToBytesIgnoresExtraNibble() throws Exception
    {
        String str = "12345";
        byte[] exp = new byte[] { 0x12, 0x34 };

        byte[] dst = new HexCodec().toBytes(str);
        assertArrayEquals("conversion to byte[]", exp, dst);
    }


    @Test
    public void testConversionToBytesThrowsOnNonHex() throws Exception
    {
        try
        {
            new HexCodec().toBytes("foo");
            fail("converted string with non-hex character");
        }
        catch (InvalidSourceByteException ex)
        {
            assertEquals("exception identifies incorrect byte", 'o', ex.getInvalidByte());
        }
    }
}
