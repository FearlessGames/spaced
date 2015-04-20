local effect = CreateSoundPrototype("/sound/sfx/events/rumbles/darkrumble.wav")

effect:setFalloff(0.3)
effect:setGain(0.4 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(19.0)

return effect