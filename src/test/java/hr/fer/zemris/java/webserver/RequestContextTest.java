package hr.fer.zemris.java.webserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class RequestContextTest {
    public static final Charset defaultCharset = StandardCharsets.US_ASCII;

    @Test
    public void ContextSettingDefaultMimeTypeTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        rc.addRCCookie(new RCCookie("name", "value", null, "127", "/"));
        rc.setMimeType(null);
        rc.setEncoding("US-ASCII");
        rc.setStatusCode(404);
        rc.setStatusText("ovojestatus");
        String sa = "iloveu";
        try {
            rc.write(sa.getBytes(defaultCharset));
        } catch (IOException e) {
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] input = new byte[5000];
        try {
            in.read(input);
        } catch (IOException e) {
        }
        String s = new String(input, defaultCharset);
        assertEquals(// text/html is the default mime type
                "HTTP/1.1 404 ovojestatus\nContent-Type: text/html; charset=US-ASCII\n"
                +
                "Set-cookie: name:\"value\"; Domain=127; Path=/; Http-Only\n\niloveu",
                s.trim());
    }

    @Test
    public void ContextSettingTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        rc.addRCCookie(new RCCookie("name", "value", null, "127", "/"));
        rc.setMimeType("text/plain");
        rc.setEncoding("US-ASCII");
        rc.setStatusCode(404);
        rc.setStatusText("ovojestatus");
        String sa = "iloveu";
        try {
            rc.write(sa.getBytes(defaultCharset));
            rc.write(sa.getBytes(defaultCharset));
        } catch (IOException e) {
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] input = new byte[5000];
        try {
            in.read(input);
        } catch (IOException e) {
        }
        String s = new String(input, defaultCharset);
        assertEquals(
                "HTTP/1.1 404 ovojestatus\nContent-Type: text/plain; charset=US-ASCII\n"
                        +
                        "Set-cookie: name:\"value\"; Domain=127;" +
                        " Path=/; Http-Only\n\niloveuiloveu",
                        s.trim());
    }

    @Test
    public void CookieTest() {
        RequestContext.RCCookie cookie = new RequestContext.RCCookie("name",
                "value", null, "127", "/");
        RCCookie cook2 = new RCCookie("name", "value", 25, null, null);
        assertEquals("127", cookie.getDomain());
        assertEquals("/", cookie.getPath());
        assertEquals("value", cookie.getValue());
        assertEquals("name", cookie.getName());
        assertEquals(Integer.valueOf(25), cook2.getMaxAge());
        assertEquals("name:\"value\"; Domain=127; Path=/; Http-Only",
                cookie.toString());
        assertEquals("name:\"value\"; Max-Age=25; Http-Only",
                cook2.toString());
    }

    @Test
    public void DefaultContextSettingTest() {// remember this!
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        rc.setEncoding(null);
        rc.setStatusText(null);
        rc.setMimeType("app/json");
        try {
            rc.write("jabadabadu");
            rc.write("a");
        } catch (IOException e) {
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] input = new byte[5000];
        try {
            in.read(input);
        } catch (IOException e) {
        }
        String s = new String(input, defaultCharset);
        assertEquals(
                "HTTP/1.1 200 OK\nContent-Type: app/json\n\njabadabadua",
                s.trim());
    }

    @Test
    public void DefaultContextTest() {// remember this!
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jabadabadu");
        } catch (IOException e) {
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] input = new byte[5000];
        try {
            in.read(input);
        } catch (IOException e) {
        }
        String s = new String(input, defaultCharset);
        assertEquals(
                "HTTP/1.1 200 OK\nContent-Type: text/html; charset=UTF-8\n\njabadabadu",
                s.trim());
    }

    @Test(expected = RuntimeException.class)
    public void IllegalCodeModification() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jo");
        } catch (IOException e) {
        }
        rc.setStatusCode(200);
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void IllegalCookieAdding() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jo");
        } catch (IOException e) {
        }
        rc.addRCCookie(new RCCookie("a", "a", null, null, null));
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void IllegalCookieConstruction() {
        RCCookie cookie = new RCCookie(null, "bla", null, null, null);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void IllegalCookieConstruction2() {
        RCCookie rc = new RCCookie("bla", null, null, null, null);
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void IllegalEncodingModification() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jo");
        } catch (IOException e) {
        }
        rc.setEncoding("UTF_8");
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void IllegalMessageModification() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jo");
        } catch (IOException e) {
        }
        rc.setStatusText("jok");
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void IllegalMimeModification() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        try {
            rc.write("jo");
        } catch (IOException e) {
        }
        rc.setMimeType("text/plain");
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void IllegalOutputStream() {
        RequestContext rc = new RequestContext(null, null, null, null);
        fail();
    }

    @Test
    public void ParametersGetTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, String> map = new HashMap<>();
        map.put("jao", "lao");
        map.put("kao", "kao");
        RequestContext rc = new RequestContext(out, map, null, null);
        assertEquals("lao", rc.getParameter("jao"));
        assertEquals("kao", rc.getParameter("kao"));
    }

    @Test
    public void ParametersTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, String> param = new HashMap<String, String>();
        Map<String, String> pparam = new HashMap<String, String>();
        List<RCCookie> cookies = new ArrayList<RCCookie>();
        RequestContext rc = new RequestContext(out, param, pparam, cookies);
        assertTrue(rc.getParameterNames().isEmpty());
        assertTrue(rc.getTemporaryParameterNames().isEmpty());
        assertTrue(rc.getPersistentParameterNames().isEmpty());
    }

    @Test
    public void PParametersTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        rc.setPersistentParameter("jao", "lao");
        assertEquals("lao", rc.getPersistentParameter("jao"));
        rc.removePersistentParameter("jao");
        assertTrue(rc.getPersistentParameter("jao") == null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void ReadOnlySetParam() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        Set<String> set = rc.getParameterNames();
        set.add("nono");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void ReadOnlySetPersistent() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        Set<String> set = rc.getPersistentParameterNames();
        set.add("nono");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void ReadOnlySetTemporary() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        Set<String> set = rc.getTemporaryParameterNames();
        set.add("nono");
    }

    @Test
    public void TParametersTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RequestContext rc = new RequestContext(out, null, null, null);
        rc.setTemporaryParameter("jao", "lao");
        assertEquals("lao", rc.getTemporaryParameter("jao"));
        rc.removeTemporaryParameter("jao");
        assertTrue(rc.getTemporaryParameter("jao") == null);
    }

}
