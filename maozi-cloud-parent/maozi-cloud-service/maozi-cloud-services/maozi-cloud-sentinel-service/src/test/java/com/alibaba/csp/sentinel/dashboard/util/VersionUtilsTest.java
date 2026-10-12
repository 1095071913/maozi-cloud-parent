package com.alibaba.csp.sentinel.dashboard.util;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.SentinelVersion;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VersionUtilsTest {
    @Test
    public void test() {
        Optional<SentinelVersion> version = VersionUtils.parseVersion("1.2.3");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(3, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.2");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(0, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(0, version.get().getMinorVersion());
        assertEquals(0, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.2.");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(0, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.2.3.");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(3, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.2.3.4");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(3, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(0, version.get().getMinorVersion());
        assertEquals(0, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("1.2.3-");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(3, version.get().getFixVersion());
        assertNull(version.get().getPostfix());
        
        version = VersionUtils.parseVersion("-");
        assertFalse(version.isPresent());
        
        version = VersionUtils.parseVersion("-t");
        assertFalse(version.isPresent());
        
        version = VersionUtils.parseVersion("");
        assertFalse(version.isPresent());
        
        version = VersionUtils.parseVersion(null);
        assertFalse(version.isPresent());
        
        version = VersionUtils.parseVersion("1.2.3-SNAPSHOTS");
        assertTrue(version.isPresent());
        assertEquals(1, version.get().getMajorVersion());
        assertEquals(2, version.get().getMinorVersion());
        assertEquals(3, version.get().getFixVersion());
        assertEquals("SNAPSHOTS", version.get().getPostfix());
    }
}
