package com.whezzel.perplayerinsomnia;

import java.util.List;

public final class PermissionNodes {
	public static final String TOGGLE = "perplayerinsomnia.toggle";
	public static final String ADMIN = "perplayerinsomnia.admin";

	private static final List<String> ALL = List.of(TOGGLE, ADMIN);

	private PermissionNodes() {
	}

	public static List<String> all() {
		return ALL;
	}
}