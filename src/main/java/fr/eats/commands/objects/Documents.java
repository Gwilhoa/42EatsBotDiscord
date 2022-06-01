package fr.eats.commands.objects;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.HashMap;

import static fr.eats.commands.event.BotListener.gson;

public class Documents {
	private static final String file = "42Eats/Documents.json";
	private static final TypeToken<Documents> type = new TypeToken<Documents>() {
	};
	public static Documents doc = null;
	private String ServId;
	private String ChannelAnnounceId;
	private String CommandsChannelId;

	public void setRoleId(String roleId) {
		RoleId = roleId;
	}

	private String RoleId;
	private HashMap<String, Boisson> Boissons;
	private HashMap<String, Meals> Meals;
	private HashMap<String, Menus> Menus;
	public Documents() {
		this.ServId = "700778044943499265";
		this.RoleId = "899795349902852126";
		this.ChannelAnnounceId = null;
		this.CommandsChannelId = null;
		this.Boissons = new HashMap<>();
		this.Meals = new HashMap<>();
		this.Menus = new HashMap<>();
	}

	public String getServId() {
		return ServId;
	}

	public void setServId(String servId) {
		ServId = servId;
		save();
	}

	public String getChannelAnnounceId() {
		return ChannelAnnounceId;
	}

	public void setChannelAnnounceId(String channelAnnounceId) {
		ChannelAnnounceId = channelAnnounceId;
		save();
	}

	public String getCommandsChannelId() {
		return CommandsChannelId;
	}

	public void setCommandsChannelId(String commandsChannelId) {
		CommandsChannelId = commandsChannelId;
		save();
	}

	public static Boolean isBartender(Member m)
	{
		return m.getRoles().contains(m.getGuild().getRoleById(doc.RoleId));
	}



	public static void load() {
		if (new File(file).exists()) {
			try {
				doc = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), type.getType());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				new File(file).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (doc == null)
			doc = new Documents();
	}

	private static void save() {
		if (!new File(file).exists()) {
			try {
				new File(file).createNewFile();
			} catch (IOException e) {
				return;
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			gson.toJson(doc, type.getType(), bw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
		}
	}
}
