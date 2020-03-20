package org.centerm.Tollway.database;

import io.realm.RealmObject;

public class TerminalTemp extends RealmObject {
	private String serialNumber;
	private String appid;
	private String tid;
	private String mid;
	private String currentBatch;
	private String currentTrace;
}