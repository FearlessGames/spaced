local effect = CreateSoundPrototype("/sound/sfx/Lazor/LazorBeam3.wav")

effect:setFalloff(0.5)
effect:setGain(0.14)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect