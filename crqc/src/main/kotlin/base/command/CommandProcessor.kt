package base.command

interface CommandProcessor<T: Command> {
    suspend fun execute(command: T)
}