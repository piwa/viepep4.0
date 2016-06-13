package at.ac.tuwien.infosys.viepep.connectors;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;

/**
 *
 */
public interface ViePEPSSHConnector {
    /**
     *
     * @param vm to execute the command on
     * @param command the shell command to be executed
     * @return an string array
     *  * [0] the result value
     *  * [1] the error result value
     * @throws Exception
     */
    String[] execSSHCommand(VirtualMachine vm, String command) throws Exception;

    void loadSettings();
}
