package xyz.e0zoo.beatbox

data class Sound(val assetPath: String) {
    var name: String
        private set

    init {
        val components = assetPath.split("/")
        val filename = components[components.size - 1]
        name = filename.replace(".wav", "")
    }
}