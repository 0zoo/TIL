# SwiftFormat
## 53 Rules

1. **blankLinesAtEndOfScope**

2. **blankLinesAtStartOfScope**

3. **blankLinesBetweenScopes**: 각 class, struct, enum, extension, protocol or function 앞에 공백 라인 삽입

4. **blankLinesAroundMark**: `// MARK:` 전후에 공백 라인 삽입

5. **braces** `{ }`: 
    - default(K&R): `--allman false`  
    ```
    if x {
    }
    ```
    - Allman-style: `--allman true`
    ```
    if x
    {
    }
    ```

6. **consecutiveBlankLines**: 여러 연속 공백 라인을 하나의 공백 라인으로 축소

7. **consecutiveSpaces**: 여러 개의 공백을 한 칸으로

8. **duplicateImports**: 중복된 import 제거

9. **elseOnSameLine**: `else`, `catch` or `while` keyword 전에 나오는 `}`의 위치 결정. 디폴트는 `} else {`    
(`--elseposition`옵션 - `same-line` or `next-line`)

10. **emptyBraces**: `func foo() {}`

11. **fileHeader**: Xcode의 자동 주석 헤더 블럭을 제거하거나 교체할 수 있다.

12. **hoistPatternLet**: 예) `(let foo, let bar) = baz()` -> `let (foo, bar) = baz()`

13. **indent**

14. **isEmpty**: `count == 0` check를 `isEmpty`로 변경

15. **linebreakAtEndOfFile**

16. **linebreaks**

17. **numberFormatting**

18. **ranges**

19. **redundantBackticks**

20. **redundantGet**: 불필요한 `get { }`절 삭제

21. **redundantLet**: Xcode에서 warning인 바인딩에서 무시되는 변수 `let` 또는 `var` 제거  
`let _ = resultIgnorableFunction()` ->
`_ = resultIgnorableFunction()`


22. **redundantLetError**: `catch`문에서 중복 `let error` 제거. 암묵적 선언

23. **redundantNilInit**: Optional var의 불필요한 `nil` 초기화 제거.

24. **redundantParens**: 불필요한 `( )` 제거

25. **redundantPattern**: `let (_, _) = bar` -> `let _ = bar`

26. **redundantRawValues**: enum에서 case 이름과 raw string value가 같을 경우 제거한다.

27. **redundantReturn**: single-line closures에서 `return`이 불필요한 경우 제거

28. **redundantSelf**: 클래스나 인스턴스 멤버 참조에서  `self` 제거 또는 추가.

29. **redundantVoidReturnType**: 함수 정의에서 불필요한 `Void` 리턴 타입 제거

30. **redundantInit**: `String.init("text")` -> `String("text")` 

31. **semicolons**: 코드에 영향 X인 세미콜론 제거 

32. **sortedImports**

33. **spaceAroundBraces**: 문맥상 `{...}` 주위에 공백을 추가 또는 제거한다. 

34. **spaceAroundBrackets**: `[ ... ]` 주변

35. **spaceAroundComments**: `//`와 `/* ... */` 주변

36. **spaceAroundGenerics**: `< ... >` 제네릭 주변

37. **spaceAroundOperators**: `.`, `+`, `==` 같은 infix operators 주변

38. **spaceAroundParens**: `( ... )` 주변

39. **spaceInsideBraces**: `{ ... }` 안에 공백 추가

40. **spaceInsideBrackets**: `[ ... ]` 안쪽 양 끝 공백 제거

41. **spaceInsideComments**: 주석 내부 공백 추가  
`/* no-op */` , `// ...`

42. **spaceInsideGenerics**: `< ... >` 내부 공백 제거

43. **spaceInsideParens**: `( ... )` 안쪽 양 끝 공백 제거

44. **specifiers**: 접근 지정자 순서 정상화  
`public override final func foo()` -> `final override public func foo()`

45. **strongOutlets**: `@IBOutlet`프로퍼티의 `weak` 제거.  
[Apple's recommendation](https://developer.apple.com/videos/play/wwdc2015/407/)

```swift
- @IBOutlet weak var label: UILabel!
+ @IBOutlet var label: UILabel!
```

46. **trailingClosures**: 모호할 수 있어 기본적으로는 비활성화.
```swift
- DispatchQueue.main.async(execute: {
    // do stuff
- })

+ DispatchQueue.main.async {
    // do stuff
+ }
```

47. **trailingCommas**: array나 dictionary literal에서 마지막 아이템에 `,` 추가 또는 제거

48. **trailingSpace**: 라인의 마지막 공백 제거

49. **todos**: `TODO:`, `MARK:`, `FIXME:`

50. **unusedArguments**: 사용하지 않는 함수와 클로져의 인자를 `_`로 

51. **void**: 빈 인자 목록과 반환 값을 나타내려면 `Void` vs `()`의 사용을 표준화한다.  
```swift
- let foo: () -> ()
+ let foo: () -> Void

- let bar: Void -> Void
+ let bar: () -> Void

- let baz: (Void) -> Void
+ let baz: () -> Void

- func quux() -> (Void)
+ func quux() -> Void
```

52. **wrapArguments**

53. **andOperator**: `if`, `guard`, `while` 구문 안에 `&&` -> `,` 로 교체