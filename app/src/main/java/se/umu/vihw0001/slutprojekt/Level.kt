package se.umu.vihw0001.slutprojekt

class Level(var lvl: Int) {
    lateinit var startPosition: Coordinates
    lateinit var cheese: Cheese
    lateinit var obstacles: List<Obstacle>
    lateinit var traps: List<Trap>

    private val gridSize = 40

    init {
        if (lvl == 1) {
            obstacles = obstaclesLevel1()
            traps     = trapsLevel1()
            startPosition = Coordinates(18f * gridSize,0f)
            cheese = Cheese(10, 25)
        }
    }

    private fun obstaclesLevel1() = listOf(
        Obstacle(19, 1, 5, 2),
        Obstacle(1, 7, 20, 0),
        Obstacle(1, 1, 21, 6),
        Obstacle(1, 2, 3, 7),
        Obstacle(14, 1, 3, 7),
        Obstacle(13, 1, 0, 13),
        Obstacle(1,1,6, 12),
        Obstacle(1,1,11,12),
        Obstacle(2,1,0,16),
        Obstacle(15, 1,6,18),
        Obstacle(1,11,15,7),
        Obstacle(7,1,15,9),
        Obstacle(4,2,21,13),
        Obstacle(1,2,6,19),
        Obstacle(3,1,4,21),
        Obstacle(1,6,9,22),
        Obstacle(1,3,13,19),
        Obstacle(15,1,10,24),
        Obstacle(1,12,24,12),
        Obstacle(17,1,24,11),
        Obstacle(1,6,31,5),
        Obstacle(4,1,28,4),
        Obstacle(11,1,31,6),
        Obstacle(1,2,36,0),
        Obstacle(12,1,36,1),
        Obstacle(1,13,37,11),
        Obstacle(3,1,45,9),
        Obstacle(5,1,43,16),
        Obstacle(6,1,37,21),
        Obstacle(14,1,24,22)
    )

    private fun trapsLevel1() = listOf(
        Trap(9, 3),
        Trap(4,5),
        Trap(13, 5),
        Trap(8,11),
        Trap(16,7),
        Trap(4, 19),
        Trap(19,13),
        Trap(22,22),
        Trap(29, 5),
        Trap(21,3),
        Trap(37, 8),
        Trap(46,2),
        Trap(46, 17),
        Trap(38, 12),
        Trap(38,22),
        Trap(31,25)
    )

}