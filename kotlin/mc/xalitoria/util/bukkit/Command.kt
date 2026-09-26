package mc.xalitoria.util.bukkit

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver

enum class CommandResult {
    PASS,
    FAIL;

    operator fun invoke(): Int = ordinal
}

public inline fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.nextArg(crossinline builder: () -> ArgumentBuilder<S, *>): T =
    then(builder())

public inline fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.orElse(crossinline block: (CommandContext<S>) -> Int): T =
    executes { block(it) }

public inline fun CommandContext<CommandSourceStack>.getPlayer(arg: String) =
    getArgument(arg, PlayerSelectorArgumentResolver::class.java)
        .resolve(source).firstOrNull()