package com.thebubblenetwork.bubblebungee.command.commands;

import com.google.common.base.Joiner;
import com.thebubblenetwork.api.global.java.ArgTrimmer;
import com.thebubblenetwork.api.global.ranks.Rank;
import com.thebubblenetwork.bubblebungee.BubbleBungee;
import com.thebubblenetwork.bubblebungee.command.CommandException;
import com.thebubblenetwork.bubblebungee.command.SimpleCommand;
import com.thebubblenetwork.bubblebungee.player.ProxiedBubblePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;

/**
 * The Bubble Network 2016
 * BubbleBungee
 * 17/02/2016 {08:41}
 * Created February 2016
 */
public class MessageCommand extends SimpleCommand {
    public MessageCommand() {
        super("msg", null, "/msg <player> <message>", "message", "mail", "tell", "whisper", "t", "m");
    }

    public BaseComponent[] Iexecute(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw invalidUsage();
        }
        String target = args[0];
        ProxiedBubblePlayer player = ProxiedBubblePlayer.getObject(target);
        if (player == null) {
            throw new CommandException("Player not found", this);
        }
        String message = Joiner.on(" ").join(new ArgTrimmer<>(String.class, args).trim(1));
        TextComponent space = new TextComponent(" ");
        TextComponent senderprefix = new TextComponent("[You -> " + player.getNickName() + "]");
        String name = sender.getName();
        String ranks;
        if (sender instanceof ProxiedPlayer) {
            ProxiedBubblePlayer senderplayer = ProxiedBubblePlayer.getObject(((ProxiedPlayer) sender).getUniqueId());
            name = senderplayer.getNickName();
            ranks = senderplayer.getRank().getName();
            for(Rank r: senderplayer.getSubRanks()){
                ranks += ", " + r.getName();
            }
        }
        else ranks = "Root Administrator";
        TextComponent playerprefix = new TextComponent("[" + name + " -> You]");
        playerprefix.setColor(ChatColor.GOLD);
        senderprefix.setColor(ChatColor.GOLD);
        HoverEvent messageprefixhover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GOLD + "Send a message with " + getUsage()));
        playerprefix.setHoverEvent(messageprefixhover);
        senderprefix.setHoverEvent(messageprefixhover);
        playerprefix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + name + " "));
        senderprefix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getNickName() + " "));

        HoverEvent sendermessageinfo = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Name: " + ChatColor.GRAY + name +
                "\nSent at " + ChatColor.GRAY + BubbleBungee.getInstance().getListener().format.format(new Date()) +
                "\nRank: " + ChatColor.GRAY + ranks));
        TextComponent messagetext = new TextComponent(message);
        messagetext.setHoverEvent(sendermessageinfo);

        if (player.getPlayer() != null) {
            player.getPlayer().sendMessage(playerprefix, space, messagetext);
        }

        ReplyCommand.REPLYMAP.put(sender.getName(), player.getName());
        if (!ReplyCommand.REPLYMAP.containsKey(player.getName())){
            ReplyCommand.REPLYMAP.put(player.getName(), sender.getName());
        }
        return new BaseComponent[]{senderprefix, space, messagetext};
    }
}
