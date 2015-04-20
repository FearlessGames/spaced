local effect = CreateSoundPrototype("/sound/sfx/Lazor/LazorHitLow1.wav")

effect:setFalloff(0.5)
effect:setGain(3.2)
effect:setLooping(false)
effect:setPitch(1.2)
effect:setReferenceDistance(4.0)

return effect