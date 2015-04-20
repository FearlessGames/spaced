local effect = CreateSoundPrototype("/sound/sfx/Lilheal/ChargeLow1.wav")

effect:setFalloff(0.8)
effect:setGain(0.1 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(6.0)

return effect