local effect = CreateSoundPrototype("/sound/sfx/events/rumbles/darkrumble.wav")

effect:setFalloff(1.2)
effect:setGain(0.1 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect