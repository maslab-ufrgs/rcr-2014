package main;

import agent.MASLABFireBrigade;
import agent.MASLABPoliceForce;
import agent.MASLABAmbulanceTeam;
import agent.MASLABCentre;
import agent.MASLABDummyAgent;

import java.io.IOException;

import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.registry.Registry;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.Constants;
import rescuecore2.log.Logger;

import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;

/**
 * Launcher for training agents. Lança no simulador o agente que gera o arquivo de pré processamento.
 * Para executar basta iniciar o simulador e em seguida executar esta classe. 
 * Não precisar iniciar a simulação, basta que o agente seja lançado no simulador para gerar o arquivo de treinamento.
 */
public final class LaunchMaslabPreProcessamento {

    private static final String FIRE_BRIGADE_FLAG = "-fb";
    private static final String POLICE_FORCE_FLAG = "-pf";
    private static final String AMBULANCE_TEAM_FLAG = "-at";
    private static final String CIVILIAN_FLAG = "-cv";

    private LaunchMaslabPreProcessamento() {
    }

    /**
     * Launch 'em!
     *
     * @param args The following arguments are understood: -p <port>, -h
     * <hostname>, -fb <fire brigades>, -pf <police forces>, -at
     * <ambulance teams>
     */
    public static void main(String[] args) {
        Logger.setLogContext("sample");
        try {
            Registry.SYSTEM_REGISTRY
                    .registerEntityFactory(StandardEntityFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY
                    .registerMessageFactory(StandardMessageFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY
                    .registerPropertyFactory(StandardPropertyFactory.INSTANCE);
            Config config = new Config();
            args = CommandLineOptions.processArgs(args, config);
            int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY,
                    Constants.DEFAULT_KERNEL_PORT_NUMBER);
            String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY,
                    Constants.DEFAULT_KERNEL_HOST_NAME);
            int fb = 1;
            int pf = 1;
            int at = 1;
            // CHECKSTYLE:ON:ModifiedControlVariable
            ComponentLauncher launcher = new TCPComponentLauncher(host, port, config);
            connect(launcher, fb, pf, at, config);
        } catch (IOException e) {
            Logger.error("Error connecting agents", e);
        } catch (ConfigException e) {
            Logger.error("Configuration error", e);
        } catch (ConnectionException e) {
            Logger.error("Error connecting agents", e);
        } catch (InterruptedException e) {
            Logger.error("Error connecting agents", e);
        }
    }

    private static void connect(ComponentLauncher launcher, int fb, int pf,
            int at, Config config) throws InterruptedException,
            ConnectionException {
        int i = 0;
        try {
            while (fb-- != 0) {
                Logger.info("Connecting fire brigade " + (i++) + "...");
                launcher.connect(new MASLABFireBrigade(1));
                Logger.info("success");
            }
        } catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
		try {
			while (true) {
				Logger.info("Connecting dummy agent " + (i++) + "...");
				launcher.connect(new MASLABDummyAgent());
				Logger.info("success");
			}
		} catch (ComponentConnectionException e) {
			Logger.info("failed: " + e.getMessage());
		}
    }
}
