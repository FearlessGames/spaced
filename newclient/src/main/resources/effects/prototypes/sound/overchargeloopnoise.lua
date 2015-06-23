local effect = CreateSoundPrototype("/sound/sfx/events/loops/softhum1.wav")

effect:setFalloff(1.4)
effect:setGain(1.5 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(1.5)

return effect