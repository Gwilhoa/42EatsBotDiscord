/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   activity.java                                      :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:05:12 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/23 16:15:23 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.eats.commands.objects;

import net.dv8tion.jda.internal.entities.ActivityImpl;

/**
 * sert à changer l'activité du bot
 */
public class activity extends ActivityImpl {

	public activity(String name) {
		super(name);
	}

	public activity(String name, String url) {
		super(name, url);
	}

	public activity(String name, String url, ActivityType type) {
		super(name, url, type);
	}

	public activity(String name, String url, ActivityType type, Timestamps timestamps, Emoji emoji) {
		super(name, url, type, timestamps, emoji);
	}
}
