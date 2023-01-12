package org.wildfly.extras.creaper.commands.logging;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wildfly.extras.creaper.XmlAssert;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineOptions;

import java.io.File;

import static org.wildfly.extras.creaper.XmlAssert.assertXmlIdentical;

public class AddConsoleLogHandlerOfflineTest {
    private static final String HANDLER_ORIGINAL = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "             %s"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String HANDLER_ADD_EXPECTED = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <console-handler name=\"consolehandler\" autoflush=\"false\" enabled=\"false\">\n"
            + "                <level name=\"FINEST\"/>\n"
            + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
            + "                <encoding value=\"UTF-8\"/>\n"
            + "                <formatter>\n"
            + "                    <named-formatter name=\"PATTERN\"/>\n"
            + "                </formatter>\n"
            + "                <target name=\"System.out\"/>\n"
            + "            </console-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void setUp() {
        XmlAssert.setNormalizeWhitespace(true);
    }

    @Test
    public void addNew() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        String loggingXmlOriginal = String.format(HANDLER_ORIGINAL, "");

        Files.write(loggingXmlOriginal, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddConsoleLogHandler addConsoleHandler = Logging.handler().console().add("consolehandler")
                .level(LogLevel.FINEST)
                .filter("match(\"filter*\")")
                .autoFlush(false)
                .enabled(false)
                .namedFormatter("PATTERN")
                .target(ConsoleTarget.STDOUT)
                .encoding(Charsets.UTF_8)
                .build();

        assertXmlIdentical(loggingXmlOriginal, Files.toString(cfg, Charsets.UTF_8));

        client.apply(addConsoleHandler);

        assertXmlIdentical(HANDLER_ADD_EXPECTED, Files.toString(cfg, Charsets.UTF_8));
    }


    @Test
    public void replaceExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        String loggingXmlOriginal = String.format(HANDLER_ORIGINAL, ""
                + "            <console-handler name=\"consolehandler\" autoflush=\"true\" enabled=\"true\">\n"
                + "                <level name=\"FINE\"/>\n"
                + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
                + "                <encoding value=\"UTF-8\"/>\n"
                + "                <target name=\"System.err\"/>\n"
                + "            </console-handler>\n"
        );

        Files.write(loggingXmlOriginal, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddConsoleLogHandler addConsoleHandler = Logging.handler().console().add("consolehandler")
                .level(LogLevel.FINEST)
                .filter("match(\"filter*\")")
                .autoFlush(false)
                .enabled(false)
                .namedFormatter("PATTERN")
                .target(ConsoleTarget.STDOUT)
                .encoding(Charsets.UTF_8)
                .replaceExisting()
                .build();

        assertXmlIdentical(loggingXmlOriginal, Files.toString(cfg, Charsets.UTF_8));

        client.apply(addConsoleHandler);

        assertXmlIdentical(HANDLER_ADD_EXPECTED, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test(expected = CommandFailedException.class)
    public void replaceExisting2() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        String loggingXmlOriginal = String.format(HANDLER_ORIGINAL, ""
                + "            <console-handler name=\"consolehandler\" autoflush=\"false\" enabled=\"false\">\n"
                + "                <level name=\"FINEST\"/>\n"
                + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
                + "                <encoding value=\"UTF-8\"/>\n"
                + "                <target name=\"System.out\"/>\n"
                + "            </console-handler>\n"
        );

        Files.write(loggingXmlOriginal, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddConsoleLogHandler addConsoleHandler = Logging.handler().console().add("consolehandler")
                .level(LogLevel.FINEST)
                .filter("match(\"filter*\")")
                .autoFlush(false)
                .enabled(false)
                .namedFormatter("PATTERN")
                .target(ConsoleTarget.STDOUT)
                .encoding(Charsets.UTF_8)
                .build();

        assertXmlIdentical(loggingXmlOriginal, Files.toString(cfg, Charsets.UTF_8));

        client.apply(addConsoleHandler);
    }
}
