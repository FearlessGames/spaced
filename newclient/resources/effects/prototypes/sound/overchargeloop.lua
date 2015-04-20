local effect = CreateSoundPrototype("/sound/sfx/events/loops/pumptone.wav")

effect:setFalloff(0.5)
effect:setGain(0.08 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(4.0)

return effect