package com.github.szabogabriel.ppp;

import java.util.Optional;

public class RuntimeArguments {

	public static enum ServerType {
		PUSH, PULL,;

		public static Optional<ServerType> byName(String name) {
			Optional<ServerType> ret = Optional.empty();
			for (ServerType it : values()) {
				if (it.name().equalsIgnoreCase(name)) {
					ret = Optional.of(it);
				}
			}
			return ret;
		}
	}

	private ServerType serverType = null;

	private int requestPort = 8080;

	private String forwardHost = "localhost";
	private int forwardPort = 8081;

	private String targetHost = "localhost";
	private int targetPort = 18080;

	public RuntimeArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (("--serverType".equals(args[i]) || "-st".equals(args[i])) && args.length > i + 1) {
				String type = args[i + 1];
				serverType = ServerType.byName(type).orElse(null);
			} else if (("--requestPort".equals(args[i]) || "-rp".equals(args[i])) && args.length > i + 1) {
				String port = args[i + 1];
				try {
					requestPort = Integer.parseInt(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (("--forwardHost".equals(args[i]) || "-fh".equals(args[i])) && args.length > i + 1) {
				forwardHost = args[i + 1];
			} else if (("--forwardPort".equals(args[i]) || "-fp".equals(args[i])) && args.length > i + 1) {
				String port = args[i + 1];
				try {
					forwardPort = Integer.parseInt(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (("--targetHost".equals(args[i]) || "-th".equals(args[i])) && args.length > i + 1) {
				targetHost = args[i + 1];
			} else if (("--targetPort".equals(args[i]) || "-tp".equals(args[i])) && args.length > i + 1) {
				String port = args[i + 1];
				try {
					targetPort = Integer.parseInt(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("--help".equals(args[i]) || "-h".equals(args[i])) {
				printHelp();
				System.exit(0);
			}
		}
		
		if (serverType == null) {
			printHelp();
			System.exit(-1);
		}
	}
	
	private void printHelp() {
		System.out.println(
				"\nPush-Pull-Proxy implementation. It starts an instance of either the push or the\n"
				+ "pull part. There are default values set, so the minimum amount of attributes you\n"
				+ "have to define is the server type.\n"
				+ "\n"
				+ "\t--serverType | -st         Defines the type of server to be started. The valid\n"
				+ "\t                           values are >>push<< or >>pull<< The servers won't start\n"
				+ "\t                           without setting this value.\n"
				+ "\n"
				+ "\t--requestPort | -rp        The outside port of the reverse proxy part (push). The\n"
				+ "\t                           default value is 8080.\n"
				+ "\n"
				+ "\t--forwardPort | -fp        Forward port of the push-pull-proxy. This value should\n"
				+ "\t                           be set for both the push and pull server as well, since\n"
				+ "\t                           the push server needs to listen to it and the pull server\n"
				+ "\t                           needs to connect to the push server. The default value is\n"
				+ "\t                           8081.\n"
				+ "\n"
				+ "\t--forwardHost | -fh        Forward host is the host name where the push proxy is\n"
				+ "\t                           listening and the pull proxy is connecting to. Hence it\n"
				+ "\t                           should be set for both servers. The default value is\n"
				+ "\t                           localhost.\n"
				+ "\n"
				+ "\t--targetPort | -tp         The target port is where the pull proxy part is forwarding\n"
				+ "\t                           the requests. The default value is 18080.\n"
				+ "\n"
				+ "\t--targetHost | -th         The target host is where the pull proxy part is forwardin\n"
				+ "\t                           the requests. The default value is localhost.\n"
				+ "\n"
				+ "\t--help | -h                Prints this help and exists the application.");
	}

	public ServerType getServerType() {
		return serverType;
	}

	public int getRequestPort() {
		return requestPort;
	}

	public String getForwardHost() {
		return forwardHost;
	}

	public int getForwardPort() {
		return forwardPort;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

}
