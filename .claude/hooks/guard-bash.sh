#!/bin/bash
COMMAND=$(jq -r '.tool_input.command')

if echo "$COMMAND" | grep -Eiq 'terraform (apply|destroy)|git push.* (--force|-f)( |$)|rm -rf|drop (table|database)'; then
  jq -n '{hookSpecificOutput:{hookEventName:"PreToolUse",permissionDecision:"deny",permissionDecisionReason:"되돌리기 어려운 명령이라 차단됨. 계획을 보고하고 사용자 승인을 받을 것"}}'
else
  exit 0
fi
