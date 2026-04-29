package me.ehsanmna.skygenerators.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
   static MiniMessage miniMessage = MiniMessage.builder().tags(TagResolver.resolver(new TagResolver[]{TagResolver.standard()})).build();

//   public static String toComponent(String content, TagResolver... placeholders) {
//      return content;
//   }

   public static Component toComponent(String content, TagResolver... placeholders) {
      return (Component.empty().decoration(TextDecoration.ITALIC, false)).append(miniMessage.deserialize(content, placeholders));
   }

   public static List<Component> toComponent(List<String> content, TagResolver... placeholders) {
      List<Component> componentContent = new ArrayList<>();
      for (String s : content) componentContent.add(toComponent(s));
      return componentContent;
   }

}
