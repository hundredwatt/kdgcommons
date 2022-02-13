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

package com.kdgregory.kdgcommons.bean;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

import com.kdgregory.kdgcommons.testinternals.InaccessibleClass;


public class TestIntrospection
{

//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    @Test
    public void testSimpleBean() throws Exception
    {
        Introspection ispec = new Introspection(SimpleBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(3, props.size());
        assertTrue(props.contains("SVal"));
        assertTrue(props.contains("IVal"));
        assertTrue(props.contains("BVal"));

        assertEquals(String.class, ispec.type("sval"));
        assertEquals(Integer.TYPE, ispec.type("ival"));
        assertEquals(Boolean.TYPE, ispec.type("bval"));

        Method getSVal = ispec.getter("sval");
        assertEquals("getSVal", getSVal.getName());
        assertEquals(String.class, getSVal.getReturnType());

        Method setSVal = ispec.setter("sval");
        assertEquals("setSVal", setSVal.getName());
        assertEquals(String.class, setSVal.getParameterTypes()[0]);

        Method getIVal = ispec.getter("ival");
        assertEquals("getIVal", getIVal.getName());
        assertEquals(Integer.TYPE, getIVal.getReturnType());

        Method setIVal = ispec.setter("ival");
        assertEquals("setIVal", setIVal.getName());
        assertEquals(Integer.TYPE, setIVal.getParameterTypes()[0]);

        Method getBVal = ispec.getter("bval");
        assertEquals("isBVal", getBVal.getName());
        assertEquals(Boolean.TYPE, getBVal.getReturnType());

        Method setBVal = ispec.setter("bval");
        assertEquals("setBVal", setBVal.getName());
        assertEquals(Boolean.TYPE, setBVal.getParameterTypes()[0]);
    }


    @Test
    public void testPropertyNamesIgnoreCase() throws Exception
    {
        Introspection ispec = new Introspection(SimpleBean.class);

        Set<String> props = ispec.propertyNames();
        assertTrue(props.contains("SVal"));
        assertFalse(props.contains("sval"));
        assertFalse(props.contains("SVAL"));

        assertEquals(String.class, ispec.type("SVal"));
        assertEquals(String.class, ispec.type("sval"));
        assertEquals(String.class, ispec.type("SVAL"));

        Method g1 = ispec.getter("SVal");
        Method g2 = ispec.getter("sval");
        Method g3 = ispec.getter("SVAL");
        assertNotNull(g1);
        assertSame(g1, g2);
        assertSame(g1, g3);

        Method s1 = ispec.setter("SVal");
        Method s2 = ispec.setter("sval");
        Method s3 = ispec.setter("SVAL");
        assertNotNull(s1);
        assertSame(s1, s2);
        assertSame(s1, s3);
    }



    @Test
    public void testMixingPrimitiveAndWrappers() throws Exception
    {
        Introspection ispec = new Introspection(MixedPrimitiveAndWrapperBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(1, props.size());
        assertTrue(props.contains("IVal"));

        assertEquals(Integer.class, ispec.type("ival"));

        Method getIVal = ispec.getter("ival");
        assertEquals("getIVal", getIVal.getName());
        assertEquals(Integer.class, getIVal.getReturnType());

        Method setIVal = ispec.setter("ival");
        assertEquals("setIVal", setIVal.getName());
        assertEquals(Integer.TYPE, setIVal.getParameterTypes()[0]);
    }


    @Test
    public void testSetterParameterRanking() throws Exception
    {
        Introspection ispec = new Introspection(MultipleSetterBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(6, props.size());
        assertTrue(props.contains("propS1"));
        assertTrue(props.contains("propS2"));
        assertTrue(props.contains("propI1"));
        assertTrue(props.contains("propI2"));
        assertTrue(props.contains("propI3"));
        assertTrue(props.contains("propI4"));

        Method setPropS1 = ispec.setter("propS1");
        assertEquals("setPropS1", setPropS1.getName());
        assertEquals(String.class, setPropS1.getParameterTypes()[0]);

        Method setPropS2 = ispec.setter("propS2");
        assertEquals("setPropS2", setPropS2.getName());
        assertEquals(Object.class, setPropS2.getParameterTypes()[0]);

        Method setPropI1 = ispec.setter("propI1");
        assertEquals("setPropI1", setPropI1.getName());
        assertEquals(Integer.TYPE, setPropI1.getParameterTypes()[0]);

        Method setPropI2 = ispec.setter("propI2");
        assertEquals("setPropI2", setPropI2.getName());
        assertEquals(Integer.class, setPropI2.getParameterTypes()[0]);

        Method setPropI3 = ispec.setter("propI3");
        assertEquals("setPropI3", setPropI3.getName());
        assertEquals(String.class, setPropI3.getParameterTypes()[0]);

        Method setPropI4 = ispec.setter("propI4");
        assertEquals("setPropI4", setPropI4.getName());
        assertEquals(Object.class, setPropI4.getParameterTypes()[0]);
    }


    @Test
    public void testSubclassSetterOverride() throws Exception
    {
        Introspection ispec = new Introspection(OverrideChildBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(1, props.size());
        assertTrue(props.contains("IVal"));

        Method setIVal = ispec.setter("ival");
        assertEquals("setIVal", setIVal.getName());
        assertEquals(String.class, setIVal.getParameterTypes()[0]);
    }


    @Test
    public void testSubclassGetterOverride() throws Exception
    {
        Introspection ispec = new Introspection(OverrideChildBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(1, props.size());
        assertTrue(props.contains("IVal"));

        Method getIVal = ispec.getter("ival");
        assertEquals("getIVal", getIVal.getName());
        assertEquals(Integer.class, getIVal.getReturnType());
    }


    @Test
    public void testMissingGetter() throws Exception
    {
        Introspection ispec = new Introspection(MissingGetterBean.class);

        Set<String> props = ispec.propertyNames();
        assertEquals(2, props.size());
        assertTrue(props.contains("propS1"));
        assertTrue(props.contains("propS2"));

        assertNotNull(ispec.getter("propS1"));
        assertNotNull(ispec.setter("propS1"));
        assertEquals(String.class, ispec.type("propS1"));

        assertNull(ispec.getter("propS2"));
        assertNotNull(ispec.setter("propS2"));
        assertNull(ispec.type("propS2"));
    }


    @Test
    public void testInvalidParameterCounts() throws Exception
    {
        Introspection ispec = new Introspection(InvalidMethodBean.class);

        // note that a property gets added to the list if it has either a
        // valid setter or a valid getter; BVal has neither
        Set<String> props = ispec.propertyNames();
        assertEquals(2, props.size());
        assertTrue(props.contains("SVal"));
        assertTrue(props.contains("IVal"));
        assertFalse(props.contains("BVal"));

        Method getSVal = ispec.getter("sval");
        assertNull(getSVal);

        Method setSVal = ispec.setter("sval");
        assertEquals("setSVal", setSVal.getName());

        Method getIVal = ispec.getter("ival");
        assertEquals("getIVal", getIVal.getName());
        assertEquals(Integer.TYPE, getIVal.getReturnType());

        Method setIVal = ispec.setter("ival");
        assertNull(setIVal);

        Method getBVal = ispec.getter("bval");
        assertNull(getBVal);

        Method setBVal = ispec.setter("bval");
        assertNull(setBVal);
    }


    @Test
    public void testPublicMethodInPrivateClass() throws Exception
    {
        InaccessibleClass instance = InaccessibleClass.newInstance();

        // the default introspector does not make the method accessible, so
        // attempting to invoke it will throw
        Introspection ispec1 = new Introspection(instance.getClass());
        Method setter1 = ispec1.setter("value");
        assertNotNull("able to retrieve setter", setter1);

        try
        {
            setter1.invoke(instance, "foo");
            fail("apparently able to invoke inaccessible setter (maybe JVM changed?)");
        }
        catch (Exception ex)
        {
            assertEquals("expected exception type", IllegalAccessException.class, ex.getClass());
        }

        // the alternate constructor allows us to force accessibility

        Introspection ispec2 = new Introspection(instance.getClass(), true);
        Method setter2 = ispec2.setter("value");
        assertNotNull("able to retrieve setter", setter2);
        setter2.invoke(instance, "foo");
        assertEquals("able to invoke setter", "foo", instance.getValue());
    }

//----------------------------------------------------------------------------
//  Classes to introspect -- all must be public and static
//----------------------------------------------------------------------------

    /**
     *  A simple bean class, with both primitive and object properties, and
     *  getters that use both "get" and "is".
     */
    public static class SimpleBean
    {
        private String sVal;
        private int iVal;
        private boolean bVal;

        public String getSVal()             { return sVal; }
        public void setSVal(String val)     { sVal = val; }

        public int getIVal()                { return iVal; }
        public void setIVal(int val)        { iVal = val; }

        public boolean isBVal()             { return bVal; }
        public void setBVal(boolean val)    { bVal = val; }
    }


    /**
     *  A class that violates the bean spec by providing getters and setters
     *  with different types.
     */
    public static class MixedPrimitiveAndWrapperBean
    {
        private Integer iVal;

        public Integer getIVal()        { return iVal; }
        public void setIVal(int val)    { iVal = Integer.valueOf(val); }
    }


    /**
     *  A class that violates the bean spec by having methods with too many
     *  or too few parameters.
     */
    public static class InvalidMethodBean
    {
        private String sVal;
        private int iVal;
        private boolean bVal;

        public String getSVal(String s)     { return sVal; }
        public void setSVal(String val)     { sVal = val; }

        public int getIVal()                { return iVal; }
        public void setIVal(int v1, int v2) { iVal = v1 + v2; }

        public boolean isBVal(boolean b)    { return bVal; }
        public void setBVal()               { bVal = true; }
    }


    /**
     *  A class that provides a variety of setters for its values.
     */
    public static class MultipleSetterBean
    {
        private String propS1;
        private String propS2;
        private Integer propI1;
        private Integer propI2;
        private Integer propI3;
        private Integer propI4;

        public String getPropS1()           { return propS1; }
        public void setPropS1(String val)   { propS1 = val; }
        public void setPropS1(Object val)   { propS1 = String.valueOf(val); }

        public String getPropS2()           { return propS2; }
        public void setPropS2(Object val)   { propS2 = String.valueOf(val); }

        public Integer getPropI1()          { return propI1; }
        public void setPropI1(Integer val)  { propI1 = val; }
        public void setPropI1(int val)      { propI1 = Integer.valueOf(val); }

        public Integer getPropI2()          { return propI2; }
        public void setPropI2(String val)   { propI2 = Integer.valueOf(val); }
        public void setPropI2(Integer val)  { propI2 = val; }

        public Integer getPropI3()          { return propI3; }
        public void setPropI3(String val)   { propI3 = Integer.valueOf(val); }
        public void setPropI3(Object val)   { propI3 = Integer.valueOf(String.valueOf(val)); }

        public Integer getPropI4()          { return propI4; }
        public void setPropI4(Object val)   { propI4 = Integer.valueOf(String.valueOf(val)); }
    }


    /**
     *  Base class for override tests.
     */
    public static class OverrideBaseBean
    {
        protected Integer iVal;

        public Number getIVal()             { return iVal; }
        public void setIVal(Integer val)    { iVal = val; }
    }


    /**
     *  Subclass for override tests.
     */
    public static class OverrideChildBean
    extends OverrideBaseBean
    {
        @Override
        public Integer getIVal()           { return iVal; }
        public void setIVal(String val)    { iVal = Integer.valueOf(val); }
    }


    /**
     *  This class has setters for all fields, but is missing a getter. The
     *  introspector should ignore that field.
     */
    public static class MissingGetterBean
    {
        private String propS1;

        @SuppressWarnings("unused")
        private String propS2;

        public String getPropS1()           { return propS1; }
        public void setPropS1(String val)   { propS1 = val; }

        public void setPropS2(String val)   { propS2 = val; }
    }
}
