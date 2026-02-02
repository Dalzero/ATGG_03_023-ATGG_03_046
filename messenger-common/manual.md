### messenger-common

---

#### Message
header와 data
- Header
    - type: 어떤 요청/응답인지 구분
    - timestamp: 메시지 생성 시각
    - sessionId: 로그인 후 발급 세션, 로그인 요청을 제외하면 대부분 필요
    - success: 응답일 때만 의미있음 (성공 or 실패)
- ErrorBody
  - 에러 코드와 에러 메시지를 통인된 형태로 전당하기 위한 구조??? 

---

#### PacketUtils
- 보내기
  - Message 객체 -> JSON 바이트로 직렬화(UTF-8)
  - 그 바이트 길이를 계산해서 `message-length: N\n" + payload`(JSON bytes) 형태로 만들어서 socket에 씀
  
- 받기
  - socket에서 \n까지 읽어서 N을 파싱
  - 그 다음 정확히 N바이트를 readFully로 읽음(중간에 덜 읽히는 경우에 대한 처리)
  - 읽은 byte를 JSON으로 역직렬화해서 Message로 만듬

> framing + json 처리는 "단일 구현체"

---

#### ObjectMapper
Jackson
- Intant(시간), UUID(sessionId) 같은 타입을 JSON으로 안전하게 주고받기 위해 구현
- 최소 요구
  - JavaTimeModule 등록(Instant 처리)
  - UTF-8 / 날짜 형식 통일
  - 알 수 없는 필드 무시 등 호환성 옵션

#### data 구조 정의(DTO 또는 규격)
common에서 "data"에 어떤 형태가 들어가야 하는지를 미리 정의

**방법 2가지 중 하나 선택**
- DTO 클래스를 common에 구현 (예: LoginRequest, ChatRoomCreateRequest)
- data를 Map/JsonNode로만 받고 필요한 곳에서 변환

외부로 공개되는 스펙(요청/응답)은 DTO로 명확히 정의
내부적으로는 JsonNode로 받아서 서비스/핸들러에서 DTO로 변환

---

#### 추가구현
이벤트 시스템
메시지 암호화/서명/압축 등 프로토콜 확장
파일 전송 / 대용량 스트리밍을 위한 별도 프레임 규격


---

---

## common

1) com.joeshim.common.ProtocolConstants

기본 요구사항에서 해야 할 것
•	Length-Prefix 프로토콜에서 쓰는 “고정 문자열”을 상수로 통일
•	예:
•	MESSAGE_LENGTH_PREFIX = "message-length:"
•	NEWLINE = "\n"
•	(필요하면) HEADER_SEPARATOR = " " 같은 것

완료 기준
•	PacketUtils가 문자열 하드코딩 없이 ProtocolConstants만 사용하게 됨.

⸻

2) com.joeshim.common.Header

기본 요구사항에서 해야 할 것
•	서버/클라가 주고받는 header 필드 확정 + getter/setter

필수 필드(스펙상)
•	String type : 실제 wire type 문자열(예: "LOGIN", "CHAT-ROOM-CREATE")
•	Instant timestamp
•	String sessionId (또는 UUID. 너 클라에서 String 쓰면 String 유지 추천)
•	Boolean success (응답만: true/false)

완료 기준
•	어떤 Message든 header가 이 필드를 동일하게 가지며 JSON으로 정상 직렬화/역직렬화됨.

⸻

3) com.joeshim.common.Message

기본 요구사항에서 해야 할 것
•	Envelope(전체 메시지) 구조 확정

필수 구성
•	Header header
•	Object data 또는 Map<String,Object> 또는 JsonNode data
•	addData(String key, Object value) (너가 data를 Map으로 쓰면 이게 편함)

완료 기준
•	클라이언트에서 Message 만들고 addData()로 data 채워서 PacketUtils로 전송 가능
•	서버에서 수신한 JSON이 Message로 역직렬화되어 header/data에 접근 가능

⸻

4) com.joeshim.common.dto.error.ErrorBody

기본 요구사항에서 해야 할 것
•	ERROR 응답의 data 규격을 통일하는 클래스

필수 필드
•	String code
•	String message

완료 기준
•	서버에서 에러가 나면 항상 type="ERROR", success=false, data=ErrorBody(code,message) 형태로 내려줄 수 있음
•	클라는 ERROR를 받으면 data를 ErrorBody로 파싱해 출력할 수 있음

⸻

5) com.joeshim.common.PacketUtils

기본 요구사항에서 common의 “최우선 핵심 파일”
여기서 해야 할 일은 2개 뿐인데, 완벽해야 함.

(1) 송신 프레임 생성
•	formatMessage(Message msg) 또는 유사 메서드
•	해야 할 것:
•	msg를 JSON(UTF-8 bytes)로 직렬화
•	payloadBytes.length를 N으로 계산(문자열 길이 말고 바이트 길이)
•	"message-length: N\n" + payloadBytes 를 합쳐 반환하거나 OutputStream에 write

(2) 수신 프레임 파싱
•	readMessage(InputStream in) 또는 유사 메서드
•	해야 할 것:
•	\n까지 읽어서 length line 파싱
•	N 바이트를 정확히 readFully로 읽기(부분 read 처리 필수)
•	JSON bytes → Message 역직렬화 후 반환

추가로 PacketUtils가 내부에서 가져야 하는 것(권장)
•	ObjectMapper(공통 설정 포함: Instant 처리)
•	parseLengthLine(), readFully() 같은 private helper

완료 기준
•	“한글 포함” 메시지에서도 length가 정확히 맞고, 송수신이 끊기지 않음
•	서버/클라 둘 다 PacketUtils만 사용하면 framing이 통일됨

⸻

6) com.joeshim.common.type 패키지 (LoginType, ChatRoomType, …)

기본 요구사항에서 해야 할 것
•	각 enum이 “스펙의 type 문자열”을 정확히 반환하게 만들기

너처럼 도메인별로 나눴다면, 최소 규칙은 이거야:
•	각 enum 상수마다 wire 문자열을 갖는다.
•	Header.type에는 항상 그 wire 문자열만 들어간다.

예시(자연어)
•	LoginType.LOGIN → “LOGIN”
•	LoginType.LOGIN_SUCCESS → “LOGIN-SUCCESS”
•	ChatRoomType.CREATE → “CHAT-ROOM-CREATE” (이름은 너 enum 상수명에 맞춰서)
•	ErrorType.ERROR → “ERROR”

완료 기준
•	클라/서버 어느 쪽이든 enum을 사용해도 “header.type 문자열”이 스펙과 1:1로 일치함
•	나중에 type 추가해도 enum에 상수만 추가하면 됨(확장성)




















