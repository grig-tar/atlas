package ut.com.grin.bitbucket.plugin.workhours;

import org.junit.Test;
import com.grin.bitbucket.plugin.workhours.api.MyPluginComponent;
import com.grin.bitbucket.plugin.workhours.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}