package org.geeks.browserdetection

/**
 * <pre>
 * {@code
 * <browser:choice>
 * <browser:isiPhone>
 *     iPhone code
 * </browser:isiPhone>
 * <browser:isBlackberry>
 *     BlackBerry code
 * </browser:isBlackberry>
 * <browser:otherwise>
 *     code for other browsers
 * </browser:otherwise>
 * </browser:choice>
 * }
 * </pre>
 */
class BrowserTagLib {

	static namespace = 'browser'

	private static def CHOICE_STACK_NAME = "${this.name}_choiceStack"

    def userAgentIdentService

	def isMobile = { attrs, body ->
        handle body, { userAgentIdentService.isMobile() }
    }

	def isNotMobile = { attrs, body ->
		handle body, { !userAgentIdentService.isMobile() }
    }

	def isiPhone = { attrs, body ->
        handle body, { userAgentIdentService.isiPhone() }
    }

	def isNotiPhone = { attrs, body ->
		handle body, { !userAgentIdentService.isiPhone() }
    }

	def isiPad = { attrs, body ->
        handle body, { userAgentIdentService.isiPad() }
    }

	def isNotiPad = { attrs, body ->
		handle body, { !userAgentIdentService.isiPad() }
    }

	def isiOS = { attrs, body ->
		handle body, { userAgentIdentService.isiOsDevice() }
    }

	def isAndroid = { attrs, body ->
		handle body, { userAgentIdentService.isAndroid() }
    }

	def isNotAndroid = { attrs, body ->
		handle body, { !userAgentIdentService.isAndroid() }
    }

	def isPalm = { attrs, body ->
        handle body, { userAgentIdentService.isPalm() }
    }

	def isNotPalm = { attrs, body ->
		handle body, { !userAgentIdentService.isPalm() }
    }

	def isWebkit = { attrs, body ->
        handle body, { userAgentIdentService.isWebkit() }
    }

	def isNotWebkit = { attrs, body ->
		handle body, { !userAgentIdentService.isWebkit() }
    }

	def isWindowsMobile = { attrs, body ->
		handle body, { userAgentIdentService.isWindowsMobile() }
    }

	def isNotWindowsMobile = { attrs, body ->
		handle body, { !userAgentIdentService.isWindowsMobile() }
    }

    def isMsie = { attrs, body ->
	    handleBrowser attrs, body, "isMsie"
    }

    def isNotMsie = { attrs, body ->
	    handle body, { !userAgentIdentService.isMsie() }
    }

	def isIE6 = { attrs, body ->
		attrs.version = "6.*"

	    handleBrowser attrs, body, "isMsie"
    }

	def isIE7 = { attrs, body ->
		attrs.version = "7.*"

	    handleBrowser attrs, body, "isMsie"
    }

	def isIE8 = { attrs, body ->
		attrs.version = "8.*"

	    handleBrowser attrs, body, "isMsie"
    }

	def isIE9 = { attrs, body ->
		attrs.version = "9.*"

	    handleBrowser attrs, body, "isMsie"
    }

    def isFirefox = { attrs, body ->
	    handleBrowser attrs, body, "isFirefox"
    }

    def isNotFirefox = { attrs, body ->
	    handle body, { !userAgentIdentService.isFirefox() }
    }

    def isChrome = { attrs, body ->
	    handleBrowser attrs, body, "isChrome"
    }

    def isNotChrome = { attrs, body ->
	    handle body, { !userAgentIdentService.isChrome() }
    }

    def isSafari = { attrs, body ->
	    handleBrowser attrs, body, "isSafari"
    }

    def isNotSafari = { attrs, body ->
	    handle body, { !userAgentIdentService.isSafari() }
    }

	def isOpera = { attrs, body ->
	    handleBrowser attrs, body, "isOpera"
    }

	def isNotOpera = { attrs, body ->
	    handle body, { !userAgentIdentService.isOpera() }
    }

	def isSeamoneky = { attrs, body ->
	    handle body, { userAgentIdentService.isSeamonkey() }
    }

	def isNotSeamonkey = { attrs, body ->
	    handle body, { !userAgentIdentService.isSeamonkey() }
    }

	/**
	 * Renders the content if browser is undefined
	 */
	def isOther = { attrs, body ->
	    handle body, { userAgentIdentService.isOther() }
    }

	/**
	 * Renders the content if browser is not undefined
	 */
	def isNotOther = { attrs, body ->
	    handle body, { userAgentIdentService.isNotOther() }
    }

    def isBlackberry = { attrs, body ->
	    handle body, { userAgentIdentService.isBlackberry() }
    }

    def isNotBlackberry = { attrs, body ->
	    handle body, { !userAgentIdentService.isBlackberry() }
    }

	def isWindows = { attrs, body ->
	    handle body, { userAgentIdentService.isWindows() }
    }

	def isNotWindows = { attrs, body ->
	    handle body, { !userAgentIdentService.isWindows() }
    }

	def isLinux = { attrs, body ->
	    handle body, { userAgentIdentService.isLinux() }
    }

	def isNotLinux = { attrs, body ->
	    handle body, { !userAgentIdentService.isLinux() }
    }

	private def handleBrowser(attrs, body, serviceMethodName){
		def version = null
		def comparisonType = null
		if(attrs.version){
			version = attrs.version
			comparisonType = ComparisonType.EQUAL
		} else if(attrs.versionLower){
			version = attrs.versionLower
			comparisonType = ComparisonType.LOWER
		} else if(attrs.versionGreater){
			version = attrs.versionGreater
			comparisonType = ComparisonType.GREATER
		}

		handle body, { userAgentIdentService."$serviceMethodName"(comparisonType, version) }
	}

	/**
	 * @param body a tag body object
	 * @param condition a closure that returns true of false
	 * @return true if
	 */
	private boolean handle(body, condition){
		def stack = getStack()
		def parent = (!stack.empty()) ? stack.peek() : null

		// skip if successful condition is met in choice tag
		if(parent && parent.levelType == HierarchyLevelType.ChoiceTag
				&& parent.successfulCondition){
			return
		}

		if(condition()){
			def conditionTagHolder = new HierarchyLevelHolder()
			conditionTagHolder.levelType = HierarchyLevelType.ConditionTag
			stack.push(conditionTagHolder)

			out << body()

			stack.pop()

			// handles the choice tag
			if(parent && parent.levelType == HierarchyLevelType.ChoiceTag){
				parent.successfulCondition = true
			}

			return true
		}

		false
	}

	def choice = { attrs, body ->
		def stack = getStack()

		if (!stack.empty() && stack.peek().levelType == HierarchyLevelType.ChoiceTag) {
			throw new IllegalStateException("choice tag can't be putted under other choice tag")
		}

		def choiceTagHolder = new HierarchyLevelHolder()
		choiceTagHolder.levelType = HierarchyLevelType.ChoiceTag
		stack.push(choiceTagHolder)

		out << body()

		// if no successful condition and otherwise tag presented,
		// execute otherwise tag
		if(!choiceTagHolder.successfulCondition && choiceTagHolder.otherwise){
			out << choiceTagHolder.otherwise()
		}

		stack.pop()
	}

	/**
	 * Returns hierarchy stack, Creates one if it does
	 * not exist yet.
	 */
	private Stack<HierarchyLevelHolder> getStack() {
		def stack = request."$CHOICE_STACK_NAME"

		if(stack == null) {
			stack = new Stack()
			request."$CHOICE_STACK_NAME" = stack
		}

		stack
	}

	/**
	 * This content is rendered in case of nothing is true in choice tag
	 */
	def otherwise = { attrs, body ->
		def stack = getStack()

		if(stack == null || stack.empty()){
			throw new IllegalStateException("otherwise tag should be under choice tag")
		}

		stack.peek().otherwise = body
	}
}

enum HierarchyLevelType {
	ChoiceTag,
	ConditionTag
}

/**
 * Describes a hierarchy level as the choice tag and
 * a condition tag
 */
class HierarchyLevelHolder {
	HierarchyLevelType levelType

	/**
	 * It makes sense only for the choice tag
	 */
	Closure otherwise
	boolean successfulCondition
}
