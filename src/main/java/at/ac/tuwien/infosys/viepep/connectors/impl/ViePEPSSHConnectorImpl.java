package at.ac.tuwien.infosys.viepep.connectors.impl;


import at.ac.tuwien.infosys.viepep.connectors.ViePEPSSHConnector;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 *
 */
@Component
@Slf4j
public class ViePEPSSHConnectorImpl implements ViePEPSSHConnector {

    private String SSH_USER_NAME;

    private boolean enabled = true;
    private String SSH_PUB_KEY_PATH;
    private String SSH_PRIV_KEY_PATH;


    @Override
    public void loadSettings() {
        Properties prop = new Properties();

        try {
            String propertyFile = System.getenv("SSH_PROPERTY_FILE");
            if (propertyFile != null) {
                prop.load(new FileInputStream(propertyFile));
            }
            else {
                propertyFile = "ssh-config/ssh.properties";
                prop.load(getClass().getClassLoader().getResourceAsStream(propertyFile));
            }

            SSH_PUB_KEY_PATH = prop.getProperty("public-key.path");
            SSH_PRIV_KEY_PATH = prop.getProperty("private-key.path");
            SSH_USER_NAME = prop.getProperty("ssh.username");

            if (SSH_PUB_KEY_PATH.isEmpty() || SSH_PRIV_KEY_PATH.isEmpty()) {
                throw new Exception("Could not find variables ");
            }
        } catch (Exception e) {
            log.error("-------- ssh properties not load, docker resizing will not work ----------------------");
            enabled = false;

        }
        log.info("\n--------------------------------------------------------------");
        log.info("\n--------ssh properties properties loaded----------------------");
        log.info("\n--------------------------------------------------------------");
    }

    @Override
    public String[] execSSHCommand(VirtualMachine vm, String command) throws Exception {
        if (!enabled) {
            throw new Exception("SSH connector not enabled");
        }
        JSch jsch = new JSch();
        String[] result = new String[2];
        try {

            jsch.addIdentity(SSH_PRIV_KEY_PATH);

            Session session = jsch.getSession(SSH_USER_NAME, vm.getIpAddress(), 22);
            session.setConfig("StrictHostKeyChecking", "no");


            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);

            OutputStream outputstream = new ByteArrayOutputStream();

            ((ChannelExec) channel).setErrStream(outputstream);

            InputStream in = channel.getInputStream();

            channel.connect();

            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            result[0] = writer.toString();
            result[1] = ""+outputstream.toString();
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
