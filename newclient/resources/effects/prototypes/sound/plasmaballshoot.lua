local effect = CreateSoundPrototype("/sound/sfx/events/loops/humnoise.wav")

effect:setFalloff(0.8)
effect:setGain(1.7 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(4.0)

return effect