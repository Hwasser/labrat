package se.umu.vihw0001.slutprojekt

class Level(lvl: Int) {
    lateinit var startPosition: Coordinates
    lateinit var cheesePosition: Coordinates
    lateinit var obstacles: List<Obstacle>
    lateinit var traps: List<Trap>

    private val gridSize = 40

    init {
        if (lvl == 1) {
            obstacles = obstaclesLevel1()
            traps     = trapsLevel1()
            startPosition = Coordinates(18f * gridSize,0f)
            cheesePosition = Coordinates(10f * gridSize, 26f * gridSize)
        }
    }

    private fun obstaclesLevel1() = listOf(
        Obstacle(19, 1, 5, 2),
        Obstacle(1, 2, 20, 0),
        Obstacle(1, 5, 21, 2),
        Obstacle(1, 2, 3, 7),
        Obstacle(14, 1, 3, 7),
        Obstacle(13, 1, 0, 13),
        Obstacle(1,2,6, 12),
        Obstacle(1,2,11,12)
    )

    private fun trapsLevel1() = listOf(
        Trap(9, 3),
        Trap(4,5),
        Trap(13, 5),
        Trap(7,11)
    )

}