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
        if (lvl == 2) {
            obstacles = obstaclesLevel2()
            traps     = trapsLevel2()
            startPosition = Coordinates(23f * gridSize,12f * gridSize)
            cheese = Cheese(46, 25  )
        }
    }

    fun isLastLevel() = (lvl == NUMBER_OF_LEVELS)

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

    private fun obstaclesLevel2() = listOf(
        Obstacle(7,3,0,0),
        Obstacle(6,1,0,12),
        Obstacle(1,4,4,13),
        Obstacle(14,1,0,17),
        Obstacle(15,1,5,20),
        Obstacle(9,1,20,19),
        Obstacle(1,9,8,6),
        Obstacle(3,1,5,7),
        Obstacle(7,1,9,12),
        Obstacle(1,2,11,15),
        Obstacle(1,5,11,0),
        Obstacle(18,1,12,5),
        Obstacle(3,3,14,6),
        Obstacle(1,1,15,13),
        Obstacle(2,1,15,13),
        Obstacle(3,1,16,14),
        Obstacle(1,6,19,15),
        Obstacle(17,1,17,2),
        Obstacle(1,13,28,6),
        Obstacle(1,3,19,8),
        Obstacle(1,3,22,6),
        Obstacle(1,3,25,8),
        Obstacle(5,1,20,11),
        Obstacle(1,2,22,12),
        Obstacle(2,1,23,14),
        Obstacle(1,24,34,3),
        Obstacle(11,1,35,9),
        Obstacle(11,1,37,18),
        Obstacle(1,4,37,0),
        Obstacle(10,1,38,2),
        Obstacle(1,1,42,3)
    )

    private fun trapsLevel2() = listOf(
        Trap(8,0),
        Trap(0,6),
        Trap(6,8),
        Trap(0,18),
        Trap(0,25),
        Trap(9,10),
        Trap(17,15),
        Trap(7,21),
        Trap(13,25),
        Trap(20,20),
        Trap(25,25),
        Trap(20,17),
        Trap(26,17),
        Trap(12,2),
        Trap(29,18),
        Trap(32,12),
        Trap(35,7),
        Trap(38,7),
        Trap(41,7),
        Trap(44,7),
        Trap(38,13),
        Trap(41,13),
        Trap(37,22),
        Trap(39,21),
        Trap(45,19),
        Trap(43,25),
        Trap(46,16)
    )

}