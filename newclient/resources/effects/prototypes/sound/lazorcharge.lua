local effect = CreateSoundPrototype("/sound/sfx/Lazor/LazorCharge1.wav")

effect:setFalloff(1.02)
effect:setGain(0.2)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.6)

return effect