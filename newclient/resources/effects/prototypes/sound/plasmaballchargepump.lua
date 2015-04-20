local effect = CreateSoundPrototype("/sound/sfx/events/loops/pumptone.wav")

effect:setFalloff(1.0)
effect:setGain(0.05 * 3)
effect:setLooping(true)
effect:setPitch(1.25)
effect:setReferenceDistance(3.0)

return effect