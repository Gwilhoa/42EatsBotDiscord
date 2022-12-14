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
	private Long timeclose;
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
	private final HashMap<String, Snack> Snacks;
	private final ArrayList<String> sauces;
	private final ArrayList<Ingredients> ingredients;
	public Documents() {
		this.ServId = "700778044943499265";
		this.RoleId = "899795349902852126";
		this.ChannelAnnounceId = null;
		this.CommandsChannelId = null;
		this.Boissons = new HashMap<>();
		this.Meals = new HashMap<>();
		this.Snacks = new HashMap<>();
		this.sauces = new ArrayList<>();
		this.ingredients = new ArrayList<>();
		this.timeclose = 0L;
	}

	public String getRoleId() {
		return RoleId;
	}

	public Long getTimeclose() {
		return timeclose;
	}

	public void setTimeclose() {
		this.timeclose = System.currentTimeMillis();
	}

	public boolean addConflict(String name, String conflict){
		int ic = 0;
		for (Ingredients i : ingredients) {
			if (i.getName().equals(name) || i.getName().equals(conflict)) {
				ic++;
			}
		}
		if (ic != 2) {
			return false;
		}
		for (Ingredients i : this.getIngredients()) {
			if (i.getName().equals(name)) {
				i.addConflict(conflict);
			}
			else if (i.getName().equals(conflict)) {
				i.addConflict(name);
			}
			save();
		}
		return true;
	}

	public List<Boisson> getAllboisson() {
		return new ArrayList<>(this.Boissons.values());
	}

	public List<Meals> getAllMeals() {
		return new ArrayList<>(this.Meals.values());
	}

	public List<Snack> getAllSnack() {return new ArrayList<>(this.Snacks.values());}

	public boolean addBoisson(String name, Double price, Double adherence){
		if (this.Boissons.get(name) != null)
			return false;
		Boisson boisson = new Boisson(price, adherence, name);
		this.Boissons.put(name,boisson);
		save();
		return true;
	}

	public ArrayList<String> getSauces() {
		return sauces;
	}

	public ArrayList<Ingredients> getIngredients() {
		return ingredients;
	}

	public boolean addSauces(String str){
		if (this.sauces.contains(str))
			return false;
		this.sauces.add(str);
		save();
		return true;
	}
	public boolean removeSauces(String str){
		if (!this.sauces.contains(str))
			return false;
		this.sauces.remove(str);
		save();
		return true;
	}

	public boolean addIngredients(String str){
		for (Ingredients ingredient : this.ingredients) {
			if (ingredient.getName().equals(str))
				return false;
		}
		this.ingredients.add(new Ingredients(str));
		save();
		return true;
	}

	public boolean removeIngredients(String str){
		if (!this.sauces.contains(str))
			return true;
		this.sauces.add(str);
		save();
		return false;
	}

	public boolean addMeals(String name, Double price, Double adherence, Boolean isingred){
		if (this.Meals.get(name) != null)
			return false;
		Meals meal = new Meals(price, adherence, name, isingred);
		this.Meals.put(name,meal);
		save();
		return true;
	}

	public boolean addMeals(String name, Double price, Double adherence, String isingred){
		if (this.Meals.get(name) != null)
			return false;
		Meals meal = new Meals(price, adherence, name, isingred.equals("1"));
		this.Meals.put(name,meal);
		save();
		return true;
	}

	public boolean addSnack(String name, Double price, Double adherence){
		if (this.Snacks.get(name) != null)
			return false;
		Snack snack = new Snack(price, adherence, name);
		this.Snacks.put(name,snack);
		save();
		return true;
	}

	public boolean removeBoisson(String name)
	{
		if (this.Boissons.get(name) == null)
			return false;
		this.Boissons.remove(name);
		save();
		return true;
	}

	public boolean removeMeals(String name)
	{
		if (this.Meals.get(name) == null)
			return false;
		this.Meals.remove(name);
		save();
		return true;
	}

	public boolean removeSnack(String name)
	{
		if (this.Snacks.get(name) == null)
			return false;
		this.Snacks.remove(name);
		save();
		return true;
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
		if (doc.RoleId == null)
			return (false);
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
