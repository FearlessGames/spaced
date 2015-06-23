local effect = CreateSoundPrototype("/sound/sfx/events/loops/pumpick.wav")

effect:setFalloff(1.4)
effect:setGain(0.12 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect