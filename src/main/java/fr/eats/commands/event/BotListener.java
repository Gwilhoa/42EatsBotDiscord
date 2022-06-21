/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/21 22:29:02 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.eats.commands.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.eats.commands.BotDiscord;
import fr.eats.commands.builder.CommandMap;
import fr.eats.commands.objects.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * capture tout les evenements du bot
 */
public class BotListener implements EventListener {

	private final CommandMap commandMap;
	private final BotDiscord bot;
	public static boolean isopen = false;

	public BotListener(CommandMap cmd, BotDiscord bot) {
		this.commandMap = cmd;
		this.bot = bot;
	}

	public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
			(JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
				if (!jsonElement.isJsonArray())
					return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
				ArrayNumber<Integer> n = new ArrayNumber<>();
				JsonArray ar = jsonElement.getAsJsonArray();
				for (int i = 0; i < ar.size(); i++)
					n.li.add(ar.get(i).getAsNumber().intValue());
				return n;
			}).create();

	@Override
	public void onEvent(GenericEvent event) {
		System.out.println(event.getClass().getSimpleName());
		if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
		else if (event instanceof MessageReceivedEvent) onMessage((MessageReceivedEvent) event);
		else if (event instanceof ButtonInteractionEvent) onButton((ButtonInteractionEvent) event);
		else if (event instanceof SelectMenuInteractionEvent) onMenu((SelectMenuInteractionEvent) event);
		else if (event instanceof SlashCommandInteraction) onSlash((SlashCommandInteraction) event);
		else if (event instanceof CommandAutoCompleteInteraction) completeCommand((CommandAutoCompleteInteraction) event);
	}



	private void onMenu(SelectMenuInteractionEvent event) {
		Message msg = event.getMessage();
		if (event.getComponent().getId().equals("choice"))
		{
			if (event.getSelectedOptions().get(0).getValue().equals("cancel")) {
				msg.getChannel().sendMessage("ta commande a été annulé").queue();
				msg.delete().queue();
			}
			if (event.getSelectedOptions().get(0).getLabel().equals("les boissons"))
				generateBoissons(msg, msg.getEmbeds().get(0).getColor().equals(Color.GREEN));
			if (event.getSelectedOptions().get(0).getLabel().equals("les plats"))
				generatePlats(msg, msg.getEmbeds().get(0).getColor().equals(Color.GREEN));
			if (event.getSelectedOptions().get(0).getLabel().equals("les snacks/desserts"))
				generateDesserts(msg, msg.getEmbeds().get(0).getColor().equals(Color.GREEN));
			if (event.getSelectedOptions().get(0).getValue().equals("finish"))
			{
				MessageEmbed me = msg.getEmbeds().get(0);
				if (me.getDescription().equals("rien, que voulez vous ?")){
					msg.getChannel().sendMessage("ta commande a été annulé").queue();
					msg.delete().queue();
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.red);
				eb.setDescription(me.getDescription());
				eb.setTitle("commande de " + me.getAuthor().getName().split(" ")[1]);
				eb.setAuthor(msg.getPrivateChannel().getUser().getId());
				if (me.getColor().equals(Color.GREEN))
					eb.setFooter(me.getFooter().getText() + " (adhérent)");
				else
					eb.setFooter(me.getFooter().getText());
				if (isopen) {
					event.getJDA().getGuildById(Documents.doc.getServId()).getTextChannelById(Documents.doc.getCommandsChannelId()).sendMessageEmbeds(eb.build()).setActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("finish", "terminé")).queue();
					msg.getChannel().sendMessage("ta commande est partis en cuisine ! ").queue();
				} else
					msg.getChannel().sendMessage("le foyer est fermé").queue();
				msg.delete().queue();
			}
		}
		if (event.getComponent().getId().equals("ingredients")){
			EmbedBuilder eb = getEmbedByMessage(msg);
			if (event.getSelectedOptions().get(0).getValue().equals("back")) {
				if (eb.getDescriptionBuilder().toString().charAt(eb.getDescriptionBuilder().toString().length() - 1) == ':'){
					String[] list = eb.getDescriptionBuilder().toString().split("\n");
					int i = 1;
					eb.setDescription(list[0] + "\n");
					while (i < list.length - 1) {
						eb.appendDescription(list[i]).appendDescription("\n");
						i++;
					}
					generatePrincipalMenu(msg, eb);
				} else {
					eb.appendDescription(" | sauces :");
					generateSauce(msg, eb, true);
				}
			}
			else {
				eb.appendDescription(" " + event.getSelectedOptions().get(0).getLabel());
				generateIngredients(msg, eb);
			}
		}
		if (event.getComponent().getId().equals("sauces")){
			EmbedBuilder eb = getEmbedByMessage(msg);
			if (event.getSelectedOptions().get(0).getValue().equals("back")) {
				if (eb.getDescriptionBuilder().toString().charAt(eb.getDescriptionBuilder().toString().length() - 1) == ':')
					eb.appendDescription(" sans sauces");
				generatePrincipalMenu(msg, eb);
			}
			else {
				eb.appendDescription(" " + event.getSelectedOptions().get(0).getLabel());
				generateSauce(msg, eb, false);
			}
		}
		if (event.getComponent().getId().equals("boisson") || event.getComponent().getId().equals("snacks")) {
			EmbedBuilder eb = getEmbedByMessage(msg);
			if (eb.getDescriptionBuilder().toString().equals("rien, que voulez vous ?") && !event.getSelectedOptions().get(0).getValue().equals("back"))
				eb.setDescription(event.getSelectedOptions().get(0).getLabel());
			else if (!event.getSelectedOptions().get(0).getValue().equals("back")) {
				eb.appendDescription("\n").appendDescription(event.getSelectedOptions().get(0).getLabel());
				eb.setFooter(calculPrice(eb));
			}
			generatePrincipalMenu(msg,eb);
		}
		if (event.getComponent().getId().equals("plats")) {
			EmbedBuilder eb = getEmbedByMessage(msg);
			if (!event.getSelectedOptions().get(0).getValue().equals("back")) {
				if (eb.getDescriptionBuilder().toString().equals("rien, que voulez vous ?") && !event.getSelectedOptions().get(0).getValue().equals("back"))
					eb.setDescription(event.getSelectedOptions().get(0).getLabel());
				else
					eb.appendDescription("\n").appendDescription(event.getSelectedOptions().get(0).getLabel());
				eb.setFooter(calculPrice(eb));
				for (Meals plat : Documents.doc.getAllMeals()) {
					if (plat.getName().equals(event.getSelectedOptions().get(0).getLabel().split(" ")[0]) && plat.isIswithingredient()) {
						eb.appendDescription(" ingredients : ");
						generateIngredients(msg, eb);
						event.deferReply().complete().deleteOriginal().queue();
						return;
					}
				}
			}
			generatePrincipalMenu(msg,eb);
		}
		event.deferReply().complete().deleteOriginal().queue();
	}

	private String calculPrice(EmbedBuilder eb) {
		String[] list = eb.getDescriptionBuilder().toString().split("\n");
		double total = 0.0;
		try {
			for (String price : list) {
				total = total + Double.parseDouble(price.split(" ")[2].replace("€", ""));
			}
		} catch (NumberFormatException e){
			total = 0.0;
		}
		return Double.toString(total);
	}

	private void generateBoissons(Message msg, boolean isadhérent){
		ArrayList<SelectOption> options = new ArrayList<>();
		if (isadhérent) {
			for (Boisson boisson : Documents.doc.getAllboisson())
				options.add(new SelectOptionImpl(boisson.getName() + " : " + boisson.getAdherencePrice() + "€", boisson.getName()));
		} else {
			for (Boisson boisson : Documents.doc.getAllboisson())
				options.add(new SelectOptionImpl(boisson.getName() + " : " + boisson.getPrice() + "€", boisson.getName()));
		}
		options.add(new SelectOptionImpl("retour", "back"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("boisson", "choisissez votre boisson", 1, 1, false, options);
		msg.editMessageComponents(ActionRow.of(selectionMenu)).queue();
	}

	private void generateDesserts(Message msg, boolean isadhérent){
		ArrayList<SelectOption> options = new ArrayList<>();
		if (isadhérent) {
			for (Snack snack : Documents.doc.getAllSnack())
				options.add(new SelectOptionImpl(snack.getName() + " : " + snack.getAdherencePrice() + "€", snack.getName()));
		} else {
			for (Snack snack : Documents.doc.getAllSnack())
				options.add(new SelectOptionImpl(snack.getName() + " : " + snack.getPrice() + "€", snack.getName()));
		}
		options.add(new SelectOptionImpl("retour", "back"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("snacks", "choisissez votre plat", 1, 1, false, options);
		msg.editMessageComponents(ActionRow.of(selectionMenu)).queue();
	}

	private void generatePlats(Message msg, boolean isadhérent){
		ArrayList<SelectOption> options = new ArrayList<>();
		if (isadhérent) {
			for (Meals meals : Documents.doc.getAllMeals())
				options.add(new SelectOptionImpl(meals.getName() + " : " + meals.getAdherencePrice() + "€", meals.getName()));
		} else {
			for (Meals meals : Documents.doc.getAllMeals())
				options.add(new SelectOptionImpl(meals.getName() + " : " + meals.getPrice() + "€", meals.getName()));
		}
		options.add(new SelectOptionImpl("retour", "back"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("plats", "choisissez votre plat", 1, 1, false, options);
		msg.editMessageComponents(ActionRow.of(selectionMenu)).queue();
	}

	private void generatePrincipalMenu(Message msg, EmbedBuilder eb) {
		eb.setFooter(calculPrice(eb));
		ArrayList<SelectOption> options = new ArrayList<>();
		options.add(new SelectOptionImpl("les boissons", "boisson list"));
		options.add(new SelectOptionImpl("les plats", "meal list"));
		options.add(new SelectOptionImpl("les snacks/desserts", "snacks list"));
		options.add(new SelectOptionImpl("fini !", "finish"));
		options.add(new SelectOptionImpl("annuler", "cancel"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("choice", "choisissez ", 1, 1, false, options);
		msg.editMessageEmbeds(eb.build()).setActionRow(selectionMenu).queue();
	}

	private void generateIngredients(Message msg, EmbedBuilder eb){
		String str = eb.getDescriptionBuilder().toString();
		str = str.split(":")[str.split(":").length - 1];
		ArrayList<String> ing = new ArrayList<>(Arrays.asList(str.split(" ")));
		ArrayList<SelectOption> options = new ArrayList<>();
		for (String ingredient : Documents.doc.getIngredients()) {
			if (!ing.contains(ingredient))
				options.add(new SelectOptionImpl(ingredient, ingredient));
		}
		if (ing.isEmpty())
			options.add(new SelectOptionImpl("annuler", "back"));
		else
			options.add(new SelectOptionImpl("c'est tout !", "back"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("ingredients", "choisissez ", 1, 1, false, options);
		msg.editMessageEmbeds(eb.build()).setActionRow(selectionMenu).queue();
	}
	private void generateSauce(Message msg, EmbedBuilder eb, boolean isfirst) {
		String str = eb.getDescriptionBuilder().toString();
		str = str.split(":")[str.split(":").length - 1];
		ArrayList<String> ing = new ArrayList<>(Arrays.asList(str.split(" ")));
		ArrayList<SelectOption> options = new ArrayList<>();
		for (String ingredient : Documents.doc.getSauces()){
			if (!ing.contains(ingredient))
				options.add(new SelectOptionImpl(ingredient, ingredient));
		}
		if (isfirst)
			options.add(new SelectOptionImpl("sans sauces", "back"));
		else
			options.add(new SelectOptionImpl("c'est tout !", "back"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("sauces", "choisissez ", 1, 1, false, options);
		msg.editMessageEmbeds(eb.build()).setActionRow(selectionMenu).queue();
	}

	private EmbedBuilder getEmbedByMessage(Message msg)
	{
		MessageEmbed Me = msg.getEmbeds().get(0);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(Me.getAuthor().getName());
		eb.setTitle(Me.getTitle());
		eb.setColor(Me.getColor());
		eb.setDescription(Me.getDescription());
		eb.setFooter(Me.getFooter().getText());
		return eb;
	}

	private void onButton(ButtonInteractionEvent event) {
		Message msg = event.getMessage();
		if (event.getButton().getId().startsWith("adherence"))
		{
			EmbedBuilder eb = new EmbedBuilder();
			if (event.getButton().getLabel().equals("oui"))
				eb.setColor(Color.green);
			else
				eb.setColor(Color.BLUE);
			eb.setTitle("votre commande :").setAuthor(msg.getEmbeds().get(0).getAuthor().getName());
			eb.setDescription("rien, que voulez vous ?").setFooter("prix : 0€");
			generatePrincipalMenu(msg, eb);
			event.deferReply().complete().deleteOriginal().queue();
		}
		if (event.getButton().getId().equals("finish")) {
			MessageEmbed me = msg.getEmbeds().get(0);
			event.getGuild().getMemberById(me.getAuthor().getName()).getUser().openPrivateChannel().complete().sendMessage("ta commande est prête").queue();
			EmbedBuilder eb = new EmbedBuilder().setTitle(me.getTitle()).setColor(Color.green).setDescription(me.getDescription()).setFooter("prix : " + me.getFooter().getText() + "€");
			event.editMessageEmbeds(eb.build()).setActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("finish", "terminé").asDisabled()).queue();
		}
	}

	private void onEnable(ReadyEvent event) {
		if (new File("42Eats/").mkdir())
			System.out.println("creating directory");
		System.out.println("le bot est lancé");
		Documents.load();
		activity act = new activity("le foyer est fermé !", null, Activity.ActivityType.WATCHING);
		event.getJDA().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
		event.getJDA().getGuilds().get(0).updateCommands().queue();
		event.getJDA().upsertCommand("addsnack", "ajoute un snack")
				.addOption(OptionType.STRING,"name", "nom du snack", true)
				.addOption(OptionType.NUMBER,"prix", "prix du snack", true)
				.addOption(OptionType.NUMBER, "adhprix", "prix pour les adhérents", true)
				.queue();
		event.getJDA().upsertCommand("setannouncechannel", "définir le channel d'annonce")
				.addOption(OptionType.STRING, "channel", "salon", true, true).queue();
	}

	private void onSlash(SlashCommandInteraction event) {
		if (event.getName().equals("addsnack")){
			Double price = event.getOption("prix").getAsDouble();
			Double ADHprice = event.getOption("adhprix").getAsDouble();
			if (Documents.doc.addSnack(event.getOption("name").getAsString(),price, ADHprice))
				event.reply("le dessert " + event.getOption("name").getAsString()).queue();
			else
				event.reply("dessert déja définis").queue();
		}
		else if (event.getName().equals("setannouncechannel"))
		{
			Documents.doc.setCommandsChannelId(event.getGuild().getTextChannelsByName(event.getOption("channel").getAsString(), false).get(0).getId());
			Documents.doc.setChannelAnnounceId(event.getGuild().getId());
			event.reply("channel d'annonce définis avec succès").queue();
		}
	}

	private void completeCommand(CommandAutoCompleteInteraction event) {
		if (event.getOptions().get(0).getName().equals("channel")) {
			ArrayList<String> ar = new ArrayList<>();
			for (TextChannel ch : event.getGuild().getTextChannels())
				ar.add(ch.getName());
			event.replyChoiceStrings(ar).queue();
		}
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		Message msg = event.getMessage();
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}
	}

	/**
	 *
	 * @param <E>
	 */
	public static class ArrayNumber<E extends Number> {
		public ArrayList<E> li = new ArrayList<>();

		public ArrayNumber() {
		}

		public ArrayNumber(E element) {
			li.add(element);
		}

		@Override
		public String toString() {
			return li.toString();
		}
	}

}
