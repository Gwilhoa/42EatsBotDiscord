package fr.eats.commands.objects;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.eats.commands.event.BotListener.gson;

public class Documents {
	private static final String file = "42Eats/Documents.json";
	private static final TypeToken<Documents> type = new TypeToken<Documents>() {
	};
	public static Documents doc;
	private String ServId;
	private String ChannelAnnounceId;
	private String CommandsChannelId;

	public void setRoleId(String roleId) {
		RoleId = roleId;
	}

	private String RoleId;
	private final HashMap<String, Boisson> Boissons;
	private final HashMap<String, Meals> Meals;
	private final HashMap<String, Menus> Menus;
	private final HashMap<String, Snack> Snacks;
	public Documents() {
		this.ServId = "700778044943499265";
		this.RoleId = "899795349902852126";
		this.ChannelAnnounceId = null;
		this.CommandsChannelId = null;
		this.Boissons = new HashMap<>();
		this.Meals = new HashMap<>();
		this.Menus = new HashMap<>();
		this.Snacks = new HashMap<>();
	}

	public List<Boisson> getAllboisson() {
		return new ArrayList<>(this.Boissons.values());
	}

	public List<Meals> getAllMeals() {
		return new ArrayList<>(this.Meals.values());
	}

	public List<Snack> getAllSnack() {return new ArrayList<>(this.Snacks.values());}

	public int addBoisson(String name, Double price, Double adherence){
		if (this.Boissons.get(name) != null)
			return -1;
		Boisson boisson = new Boisson(price, adherence, name);
		this.Boissons.put(name,boisson);
		save();
		return 1;
	}

	public int addMeals(String name, Double price, Double adherence){
		if (this.Meals.get(name) != null)
			return -1;
		Meals meal = new Meals(price, adherence, name);
		this.Meals.put(name,meal);
		save();
		return 1;
	}

	public int addSnack(String name, Double price, Double adherence){
		if (this.Snacks.get(name) != null)
			return -1;
		Snack snack = new Snack(price, adherence, name);
		this.Snacks.put(name,snack);
		save();
		return 1;
	}

	public int removeBoisson(String name)
	{
		if (this.Boissons.get(name) == null)
			return -1;
		this.Boissons.remove(name);
		save();
		return 1;
	}

	public int removeMeals(String name)
	{
		if (this.Meals.get(name) == null)
			return -1;
		this.Meals.remove(name);
		save();
		return 1;
	}

	public int removeSnack(String name)
	{
		if (this.Snacks.get(name) == null)
			return -1;
		this.Snacks.remove(name);
		save();
		return 1;
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
		return m.getRoles().contains(m.getGuild().getRoleById(doc.RoleId)) || m.getId().equals("315431392789921793");
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
		System.out.println(doc);
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

	@Override
	public String toString() {
		return "Documents{" +
				"ServId='" + ServId + '\'' +
				", ChannelAnnounceId='" + ChannelAnnounceId + '\'' +
				", CommandsChannelId='" + CommandsChannelId + '\'' +
				", RoleId='" + RoleId + '\'' +
				'}';
	}
}
