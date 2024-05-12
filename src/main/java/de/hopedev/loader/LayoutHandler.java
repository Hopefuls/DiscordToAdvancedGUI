package de.hopedev.loader;

import java.awt.Font;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.EventHandler;

import de.hopedev.Main;
import de.hopedev.objects.DiscordMessage;
import me.leoko.advancedgui.manager.ResourceManager;
import me.leoko.advancedgui.utils.LayoutExtension;
import me.leoko.advancedgui.utils.components.Component;
import me.leoko.advancedgui.utils.components.GroupComponent;
import me.leoko.advancedgui.utils.components.TextComponent;
import me.leoko.advancedgui.utils.components.TextInputComponent;
import me.leoko.advancedgui.utils.events.GuiInteractionBeginEvent;
import me.leoko.advancedgui.utils.events.GuiInteractionExitEvent;
import me.leoko.advancedgui.utils.events.LayoutLoadEvent;
import me.leoko.advancedgui.utils.interactions.Interaction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class LayoutHandler implements LayoutExtension {
    public static String SEND_BUTTON_ID = "68VFQJ4N";
    public static String TEXT_INPUT_ID = "20J4lC6t";
    public static String MESSAGES_GROUP_ID = "U6veq9QJ";
    public static ArrayList<DiscordMessage> savedMessages = new ArrayList<DiscordMessage>();
    public static ArrayList<Interaction> guiInteractions = new ArrayList<Interaction>();

    @Override
    public void onLayoutLoad(LayoutLoadEvent loadEvent) {
        System.out.println("Layout loaded");
        // Create a bukkit runnable that runs every 5 seconds
        Main.instance.getServer().getScheduler().runTaskTimer(Main.instance, () -> {
            // get last 5 messages from the discord channel
            TextChannel channel = Main.discordbot.getTextChannelById(Main.channelID);
            // clear the messages
            savedMessages.clear();
            channel.getHistory().retrievePast(5).queue(messages -> {

                // reverse the array
                // Collections.reverse(messages);
                System.out.println("Retrieved messages from discord channel: "+messages.size());
                for (Message message : messages) {
                    
                    if (message.getAuthor() == Main.discordbot.getSelfUser()) {
                        // Format: Username> Hello World everything here is text
                        // Split to get the Username there
                        String[] parts = message.getContentRaw().split("> ");
                        String author = parts[0];
                        String content = parts[1];
                        System.out.println("Author: " + author + " Content: " + content);
                        DiscordMessage discordMessage = new DiscordMessage(author + " [MC]", content);
                        savedMessages.add(discordMessage);
                    } else {
                        DiscordMessage discordMessage = new DiscordMessage(message.getAuthor().getName()+" [DISCORD]", message.getContentRaw());
                        savedMessages.add(discordMessage);
                    }

                    
                }
                for (Interaction interaction : guiInteractions) {
                    updateMessages(interaction);
                }
            });

            System.out.println("Getting messages from discord channel, next in 5");
        }, 0, 2 * 20);

    }

    @EventHandler
    public void onInteract(GuiInteractionBeginEvent event) {
        event.getInteraction().getComponentTree().locate(SEND_BUTTON_ID).setClickAction((interaction, player, primaryTrigger) -> {
            // get the text input field
            TextInputComponent textInput = (TextInputComponent) interaction.getComponentTree().locate(TEXT_INPUT_ID);
            String user_input = textInput.getInput();
            String finalMessage = player.getName() + "> " + user_input;
            
            // Main.discordbot.getTextChannelById(Main.channelID).sendMessage(finalMessage).queue();
            // Send message, disable any sort of pings at all
            MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
            messageBuilder.setContent(finalMessage);
            messageBuilder.setAllowedMentions(EnumSet.noneOf(MentionType.class));
            Main.discordbot.getTextChannelById(Main.channelID).sendMessage(messageBuilder.build()).queue();
            textInput.setInput("", false);
        });
    }

    public void updateMessages(Interaction interaction) {
        // get the messages group
        int y_offset = 435;
        int x_baseline = 15;
        System.out.println("Saved messages: " + savedMessages.size());
        if (savedMessages.size() == 0) {
            return;
        }

        ArrayList<Component> messagesAsTextComponents = new ArrayList<Component>();

        for (int i = 0; i < savedMessages.size(); i++) {
            DiscordMessage message = savedMessages.get(i);
            final Font font = ResourceManager.getInstance().getFont("gg sans Regular", 30);

            // String id, Action clickAction, boolean hidden, Interaction interaction, int x, int y, Font font, String text, Color color
            TextComponent textComponent = new TextComponent("", null, false, null, x_baseline, y_offset, font, message.toString(), java.awt.Color.WHITE);
            messagesAsTextComponents.add(textComponent);
            y_offset -= 80;
            System.out.println("Adding message to chat: " + message.toString());
        }

        GroupComponent groupComponent = (GroupComponent) interaction.getComponentTree().locate(MESSAGES_GROUP_ID);

        List<Component> components = groupComponent.getComponents();
        components.clear();

        components.addAll(messagesAsTextComponents);

    }

    @EventHandler
    public void onBeginChatting(GuiInteractionBeginEvent event) {
        guiInteractions.add(event.getInteraction());
        updateMessages(event.getInteraction());
    }
    
    public void onEndChatting(GuiInteractionExitEvent event) {
        guiInteractions.remove(event.getInteraction());
    }
}
