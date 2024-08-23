-- Lua Script: 특정 SET에서 사용자 ID가 존재하는지 확인
local draw_winner_key = KEYS[1]
local user_id = ARGV[1]  -- 사용자 ID를 문자열로 변환

-- SET에서 user_id가 존재하는지 확인
local exists = redis.call('SISMEMBER', draw_winner_key, user_id)

if exists == 1 then
    return 1
else
    return 0
end
