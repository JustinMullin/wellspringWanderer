package xyz.jmullin.ld34.model

import xyz.jmullin.drifter.RandomUtil._

object Messages {
  var messageId = 0

  val messages = List(
    "This world is better off now. Let us continue on. There is more to do.",
    "This is a worthy task, and you a worthy laborer. We thank you.",
    "If the stars could speak, we are inclined believe they would honor you.",
    "Picturesque. Another world improved. Thank you, really.",
    "The universe is large. This world is small, but better now. Let us continue.",
    "Wonderful. Thank you for your efforts. It will become easier.",
    "See? With practice you lose fewer pods. Improvement is sublime.",
    "Take just a moment to consider the stars. There is more work out there.",
    "There is much more to be done. Come, let us leave for another place.",
    "Another world sparkles with life because of you. Let us move on.",
    "You may press on, or not. That is your choice. We will continue.",
    "Our work is endless. The satisfaction is infinite.",
    "Thank you. This world is now beautiful."
  ) ::: List("This work will not end. Continue only if you desire.")

  def getLevelCompleteMessage = {
    val result = if(messageId < messages.size-1) messages(messageId) else rElement(messages)
    messageId += 1
    result
  }
}
