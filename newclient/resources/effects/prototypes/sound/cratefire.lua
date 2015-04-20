local effect = CreateSoundPrototype("/sound/sfx/events/burn/flameball.wav")

effect:setFalloff(1.5)
effect:setGain(0.4 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect