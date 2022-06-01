/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/01 21:21:23 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.eats.commands.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.eats.commands.BotDiscord;
import fr.eats.commands.builder.CommandMap;
import fr.eats.commands.objects.Documents;
import fr.eats.commands.objects.activity;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

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
