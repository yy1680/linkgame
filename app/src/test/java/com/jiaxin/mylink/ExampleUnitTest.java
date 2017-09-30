package com.jiaxin.mylink;

import com.jiaxin.mylink.Model.LinkMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testSwap(){
        LinkMap map = new LinkMap();
        map.init(12,10,10);
    }
}