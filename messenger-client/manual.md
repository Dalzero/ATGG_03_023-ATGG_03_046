
**A) main/ClientMain**

서버의 ServerMain과 똑같이 “조립만”
•	Socket 연결
•	ClientChannel 생성
•	Dispatcher/Handler 구성
•	ReceiverLoop/ConsoleLoop 스레드 시작

**B) runnable/ReceiverLoop**

서버의 ClientWorker와 대칭
•	channel.read()로 Message 수신(= PacketUtils.readMessage 내부 사용)
•	dispatcher.dispatch(message) 호출
•	예외 시 close/종료

**C) runnable/ConsoleLoop**

서버에는 없는 “입력 루프”지만 같은 레이어에 둠
•	표준 입력 읽기
•	CommandParser로 파싱
•	ClientApi 호출(요청 생성 + send)

**D) method/parser/Dispatcher + Handler + impl/**

서버의 Dispatcher/Handler 구조와 완전 동일
•	header.type으로 handler 선택
•	handler는 받은 메시지를 해석하고
•	상태 업데이트(repo)
•	출력(Printer)
•	필요하면 후속 요청(ClientApi 호출)까지 가능(권장은 아니고 필요할 때만)

**E) service/ClientApi**

서버의 AuthService 같은 “기능 묶음” 위치
•	요청 메시지 생성 규칙을 한 곳에서 통일:
•	header.type = wire string
•	header.timestamp 채우기
•	header.sessionId(state repo에서 가져오기, LOGIN 제외)
•	data 구성(addData/DTO)
•	전송은 ClientChannel로만

**F) repo/(ClientStateRepository)**

서버 repo가 세션을 저장하듯, 클라는 자기 상태를 저장
•	setSessionId, getSessionId
•	setUserId, getUserId
•	setCurrentRoomId, getCurrentRoomId

**G) method/response/Printer**

서버 Responses가 “응답 만들기”라면, 클라 Printer는 “응답 출력”
하지만 역할상 “응답 계층”이라 동일한 패키지 위치가 맞음.

**H) thread/channel/ClientChannel**

서버 ClientChennel과 완전히 대칭
•	send(Message)
•	read()
•	close()

**I) thread/pool/ThreadFactory**

서버 ThreadFactory와 대칭
•	newReceiverThread()
•	newConsoleThread()

⸻

	3.	추가구현을 위한 “끼울 자리” (지금은 구현 안 해도 됨)

	•	Observer/EventBus는 method/parser/Dispatcher 내부 구현을 “Map dispatcher → publish-subscribe”로 바꾸면 됨
	•	UI가 콘솔 → GUI로 바뀌면 runnable/ConsoleLoop만 교체하면 됨
	•	로그/저장 기능은 method/response/Printer 또는 별도 handler 추가로 붙이면 됨
