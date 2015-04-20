local effect = CreateSoundPrototype("/sound/sfx/Lazor/LazorHit2.wav")

effect:setFalloff(2.5)
effect:setGain(1.2 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect