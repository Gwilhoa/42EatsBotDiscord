/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/02 12:09:20 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.eats.commands.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.eats.commands.BotDiscord;
import fr.eats.commands.builder.CommandMap;
import fr.eats.commands.objects.Boisson;
import fr.eats.commands.objects.Documents;
import fr.eats.commands.objects.SelectOptionImpl;
import fr.eats.commands.objects.activity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
	}

	private void onMenu(SelectMenuInteractionEvent event) {
		Message msg = event.getMessage();
		if (event.getComponent().getId().equals("choice"))
		{
			if (event.getSelectedOptions().get(0).getValue().equals("cancel")) {
				msg.getChannel().sendMessage("ta commande a été annulé").queue();
				msg.delete().queue();
			}
			if (event.getSelectedOptions().get(0).getLabel().equals("boisson"))
			{
				ArrayList<SelectOption> options = new ArrayList<>();
				if (msg.getEmbeds().get(0).getColor().equals(Color.GREEN)) {
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
		}
		if (event.getComponent().getId().equals("boisson")) {
			MessageEmbed Me = msg.getEmbeds().get(0);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(Me.getAuthor().getName());
			eb.setTitle(Me.getTitle());
			eb.setColor(Me.getColor());
			if (event.getSelectedOptions().get(0).getValue().equals("back"))
				eb.setDescription(Me.getDescription());
			else if (Me.getDescription().equals("rien, que voulez vous ?"))
				eb.setDescription(event.getSelectedOptions().get(0).getLabel());
			else
				eb.setDescription(Me.getDescription()).appendDescription("\n").appendDescription(event.getSelectedOptions().get(0).getLabel());
			String[] list = eb.getDescriptionBuilder().toString().split("\n");
			Double total = 0.0;
			try {
				for (String price : list) {
					total = total + Double.parseDouble(price.split(" ")[2].replace("€", ""));
				}
			} catch (NumberFormatException e){
				total = 0.0;
			}
			eb.setFooter(total.toString());
			generatePrincipalMenu(msg, eb);
		}
		event.deferReply().complete().deleteOriginal().queue();
	}

	private void generatePrincipalMenu(Message msg, EmbedBuilder eb) {
		ArrayList<SelectOption> options = new ArrayList<>();
		options.add(new SelectOptionImpl("boisson", "boisson list"));
		options.add(new SelectOptionImpl("nourriture", "meal list"));
		options.add(new SelectOptionImpl("Menu", "menu list"));
		options.add(new SelectOptionImpl("fini !", "finish"));
		options.add(new SelectOptionImpl("annuler", "cancel"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("choice", "choisissez ", 1, 1, false, options);
		msg.editMessageEmbeds(eb.build()).setActionRow(selectionMenu).queue();
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
	}

	private void onEnable(ReadyEvent event) {
		if (new File("42Eats/").mkdir())
			System.out.println("creating directory");
		System.out.println("le bot est lancé");
		Documents.load();
		activity act = new activity("le foyer est fermé !", null, Activity.ActivityType.WATCHING);
		event.getJDA().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
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
