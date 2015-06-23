function round(num, decimals)
	if decimals then
		 local mult = 10^decimals
		 return math.floor(num * mult + 0.5) / mult
	  end
	  return math.floor(num + 0.5)
end