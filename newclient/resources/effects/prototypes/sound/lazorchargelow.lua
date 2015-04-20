local effect = CreateSoundPrototype("/sound/sfx/Lazor/ChargeLow1.wav")

effect:setFalloff(0.8)
effect:setGain(0.05)
effect:setLooping(false)
effect:setPitch(1.4)
effect:setReferenceDistance(3.0)

return effect