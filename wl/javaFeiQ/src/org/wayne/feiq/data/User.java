package org.wayne.feiq.data;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {

	private static final long serialVersionUID = 6399944506002314982L;

	private transient String id;
	private String userName;
	private String groupName = "";
	private String osUserName;
	private transient InetAddress address;
	private int udpServerPort;
	private int serverSocketPort;
	public final char separatorChar = File.separatorChar;
	public final String encode = System.getProperty("file.encoding");

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName == null ? osUserName : userName;
	}

	public User setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getGroupName() {
		return groupName;
	}

	public User setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getOsUserName() {
		return osUserName;
	}

	public User setOsUserName(String osUserName) {
		this.osUserName = osUserName;
		return this;
	}

	public InetAddress getAddress() {
		return address;
	}

	public User setAddress(InetAddress address) {
		this.address = address;
		return this;
	}

	public int getUdpServerPort() {
		return udpServerPort;
	}

	public void setUdpServerPort(int udpServerPort) {
		this.udpServerPort = udpServerPort;
	}

	public int getServerSocketPort() {
		return serverSocketPort;
	}

	public void setServerSocketPort(int serverSocketPort) {
		this.serverSocketPort = serverSocketPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}

	public String toString() {
		return "{address: " + address + ", userName: '" + userName
				+ "', groupName: '" + groupName + "', osUserName: '"
				+ osUserName + "'}";
	}

}
