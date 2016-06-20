package at.ac.tuwien.infosys.viepep.connectors.impl;

import org.apache.commons.io.IOUtils;

/**
 * <p/>
 * this is an initialize script to initializeAndUpdate a node in our swarm,
 * check on that node for output with:
 * journalctl -b -u oem-cloudinit.service --no-pager
 * if something failed
 * and use
 * systemctl status docker.service
 * systemctl status docker-tcp.socket
 * systemctl status docker-swarm.service
 * global cloud-initialize info
 * journalctl _EXE=/usr/bin/coreos-cloudinit
 */
public class FileLoader {

    private static FileLoader instance;
    public String CLOUD_INIT_DOCKER_START;
    public String CLOUD_INIT;
    public String DOCKER_RESIZE_SCRIPT;

    private FileLoader() {
        try {
            CLOUD_INIT = IOUtils.toString(
                    getClass().getClassLoader().getResourceAsStream("docker-config/swarm-node-cloud-config.yaml"),
                    "UTF-8"
            );
            CLOUD_INIT_DOCKER_START = IOUtils.toString(
                    getClass().getClassLoader().getResourceAsStream("docker-config/docker-units-script.template"),
                    "UTF-8"
            );
            DOCKER_RESIZE_SCRIPT = IOUtils.toString(
                    getClass().getClassLoader().getResourceAsStream("docker-config/docker-resize.sh"),
                    "UTF-8"
            );
        } catch (Exception e) {
            System.err.println("Could not load cloud-initialize config");
            CLOUD_INIT = "";
        }
    }

    public static FileLoader getInstance() {
        if (instance == null) {
            instance = new FileLoader();
        }
        return instance;
    }
}
