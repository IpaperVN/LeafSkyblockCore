package me.ipapervn.leafskyblockcore.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface cho mỗi command node trong tree command system.
 * Mỗi tính năng chỉ cần implement interface này trong 1 class duy nhất.
 */
public interface CommandNode {

    /**
     * Xử lý command execution.
     *
     * @param sender Người thực hiện command
     * @param args Arguments sau node name (ví dụ: /lc island create → args = ["create"])
     * @return true nếu thực hiện thành công, false nếu có lỗi
     */
    boolean execute(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Xử lý tab completion.
     *
     * @param sender Người đang tab
     * @param args Arguments hiện tại
     * @return List các suggestion, null nếu không có
     */
    @Nullable
    List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Tên của node (ví dụ: "island", "shop", "admin").
     *
     * @return Node name
     */
    @NotNull
    String getName();

    /**
     * Permission cần để sử dụng node này.
     *
     * @return Permission string, null nếu không cần permission
     */
    @Nullable
    default String getPermission() {
        return null;
    }

    /**
     * Mô tả ngắn gọn về node.
     *
     * @return Description
     */
    @NotNull
    default String getDescription() {
        return "No description";
    }
}
