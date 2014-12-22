package com.gmail.adffice.zbar;
/*
 * Copyright (c) 2014, Dallas.Cao (adffice@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.test.AndroidTestCase;

public class ImageTest extends AndroidTestCase {
    protected Image image;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        image = new Image();
    }

    @Override
    protected void tearDown() throws Exception {
        image.destroy();
        image = null;
        super.tearDown();
    }


    public void testCreation() {
        Image img0 = new Image(123, 456);
        Image img1 = new Image("BGR3");
        Image img2 = new Image(987, 654, "UYVY");

        assertEquals(123, img0.getWidth());
        assertEquals(456, img0.getHeight());
        assertEquals(null, img0.getFormat());

        assertEquals(0, img1.getWidth());
        assertEquals(0, img1.getHeight());
        assertEquals("BGR3", img1.getFormat());

        assertEquals(987, img2.getWidth());
        assertEquals(654, img2.getHeight());
        assertEquals("UYVY", img2.getFormat());
    }

    public void testSequence() {
        assertEquals(0, image.getSequence());
        image.setSequence(42);
        assertEquals(42, image.getSequence());
    }

    public void testSize() {
        assertEquals(0, image.getWidth());
        assertEquals(0, image.getHeight());

        image.setSize(640, 480);
        int[] size0 = {640, 480};
        assertEquals(size0[0], image.getSize()[0]);
        assertEquals(size0[1], image.getSize()[1]);

        int[] size1 = {320, 240};
        image.setSize(size1);
        assertEquals(320, image.getWidth());
        assertEquals(240, image.getHeight());
    }

    public void testCrop() {
        int[] zeros = {0, 0, 0, 0};
        assertArrayEquals(zeros, image.getCrop());

        image.setSize(123, 456);
        int[] crop0 = {0, 0, 123, 456};
        assertArrayEquals(crop0, image.getCrop());

        image.setCrop(1, 2, 34, 56);
        int[] crop1 = {1, 2, 34, 56};
        assertArrayEquals(crop1, image.getCrop());

        image.setCrop(-20, -20, 200, 500);
        assertArrayEquals(crop0, image.getCrop());

        int[] crop2 = {7, 8, 90, 12};
        image.setCrop(crop2);
        assertArrayEquals(crop2, image.getCrop());

        image.setSize(654, 321);
        int[] crop3 = {0, 0, 654, 321};
        assertArrayEquals(crop3, image.getCrop());

        int[] crop4 = {-10, -10, 700, 400};
        image.setCrop(crop4);
        assertArrayEquals(crop3, image.getCrop());
    }

    public void testFormat() {
        assertNull(image.getFormat());
        image.setFormat("Y800");
        assertEquals("Y800", image.getFormat());
        boolean gotException = false;
        try {
            image.setFormat("[]");
        } catch (IllegalArgumentException e) {
            // expected
            gotException = true;
        }
        assertTrue("Expected exception", gotException);
        assertEquals("Y800", image.getFormat());
    }

    public void testSetFormatInvalid0() {
        try {
            image.setFormat(null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void testSetFormatInvalid1() {
        try {
            image.setFormat("");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void testSetFormatInvalid2() {
        try {
            image.setFormat("YOMAMA");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void testSetFormatInvalid3() {
        try {
            image.setFormat("foo");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void testData() {
        assertNull(image.getData());

        int[] ints = new int[24];
        image.setData(ints);
        assertSame(ints, image.getData());

        byte[] bytes = new byte[280];
        image.setData(bytes);
        assertSame(bytes, image.getData());

        image.setData((byte[]) null);
        assertNull(image.getData());
    }

    public void testConvert() {
        image.setSize(4, 4);
        image.setFormat("RGB4");
        int[] rgb4 = new int[16];
        byte[] exp = new byte[16];
        for (int i = 0; i < 16; i++) {
            int c = i * 15;
            rgb4[i] = c | (c << 8) | (c << 16) | (c << 24);
            exp[i] = (byte) c;
        }
        image.setData(rgb4);

        Image gray = image.convert("Y800");
        assertEquals(4, gray.getWidth());
        assertEquals(4, gray.getHeight());
        assertEquals("Y800", gray.getFormat());

        byte[] y800 = gray.getData();
        assertEquals(16, y800.length);

        assertArrayEquals(exp, y800);
    }

    private void assertArrayEquals(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    private void assertArrayEquals(byte[] expected, byte[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }
}
