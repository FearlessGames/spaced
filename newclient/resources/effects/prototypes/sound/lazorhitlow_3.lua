local effect = CreateSoundPrototype("/sound/sfx/Lazor/LazorHitLow1.wav")

effect:setFalloff(0.5)
effect:setGain(6.2)
effect:setLooping(false)
effect:setPitch(0.7)
effect:setReferenceDistance(4.0)

return effect